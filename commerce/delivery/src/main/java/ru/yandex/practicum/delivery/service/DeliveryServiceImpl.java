package ru.yandex.practicum.delivery.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;
import ru.practicum.interaction.api.enums.delivery.DeliveryState;
import ru.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.practicum.interaction.api.feign.contract.OrderContract;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderContract orderContract;
    private final WarehouseContract warehouseContract;

    private static BigDecimal costByAddress(String warehouseAddress, BigDecimal BASE_RATE) {
        final String ADDRESS_1 = "ADDRESS_1";
        final String ADDRESS_2 = "ADDRESS_2";

        BigDecimal warehouseMultiplier = BigDecimal.ZERO;

        if (warehouseAddress.contains(ADDRESS_1)) {
            warehouseMultiplier = warehouseMultiplier.add(BigDecimal.ONE);
        }

        if (warehouseAddress.contains(ADDRESS_2)) {
            warehouseMultiplier = warehouseMultiplier.add(BigDecimal.valueOf(2));
        }

        return BASE_RATE.multiply(warehouseMultiplier).add(BASE_RATE);
    }

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("[Доставка] Планирование новой доставки: {}", deliveryDto);

        deliveryDto.setStatus(DeliveryState.CREATED);

        Delivery delivery = deliveryMapper.fromDeliveryDto(deliveryDto);
        deliveryRepository.save(delivery);

        log.info("[Доставка] Доставка {} успешно создана", delivery.getDeliveryId());
        return deliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        log.info("[Доставка] Завершение доставки для заказа {}", orderId);

        Delivery delivery = deliveryRepository.findByDeliveryId(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderContract.delivery(delivery.getOrderId());
        log.info("[Доставка] Доставка {} успешно завершена", delivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        log.info("[Доставка] Ошибка доставки для заказа {}", orderId);

        Delivery delivery = deliveryRepository.findByDeliveryId(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderContract.failDelivery(delivery.getOrderId());
        log.info("[Доставка] Доставка {} помечена как неуспешная", delivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        log.info("[Доставка] Получение товара для доставки: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка: " + deliveryId));

        delivery.setDeliveryState(DeliveryState.IN_DELIVERY);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(deliveryId)
                .build();

        warehouseContract.shippedToDelivery(request);
        log.info("[Доставка] Товар передан в доставку {}", deliveryId);
    }

    @Override
    @Transactional
    public BigDecimal deliveryCost(OrderDto orderDto) {
        log.info("[Доставка] Расчёт стоимости доставки для заказа {}", orderDto.getOrderId());

        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка: " + orderDto.getDeliveryId()));

        AddressDto warehouseAddressDto = warehouseContract.getWarehouseAddress();
        String warehouseAddress = warehouseAddressDto.getStreet();

        final BigDecimal BASE_RATE = BigDecimal.valueOf(5.0);
        BigDecimal step1 = costByAddress(warehouseAddress, BASE_RATE);

        BigDecimal fragileAddition = Boolean.TRUE.equals(orderDto.getFragile())
                ? step1.multiply(BigDecimal.valueOf(0.2))
                : BigDecimal.ZERO;
        BigDecimal step2 = step1.add(fragileAddition);

        BigDecimal weightAddition = BigDecimal.valueOf(orderDto.getTotalWeight())
                .multiply(BigDecimal.valueOf(0.3));
        BigDecimal step3 = step2.add(weightAddition);

        BigDecimal volumeAddition = BigDecimal.valueOf(orderDto.getTotalVolume())
                .multiply(BigDecimal.valueOf(0.2));
        BigDecimal step4 = step3.add(volumeAddition);

        String deliveryStreet = delivery.getToAddress().getStreet();
        BigDecimal addressAddition = warehouseAddress.equals(deliveryStreet)
                ? BigDecimal.ZERO
                : step4.multiply(BigDecimal.valueOf(0.2));
        BigDecimal totalCost = step4.add(addressAddition);

        log.info("[Доставка] Стоимость доставки для заказа {}: {}", orderDto.getOrderId(), totalCost);
        return totalCost;
    }
}