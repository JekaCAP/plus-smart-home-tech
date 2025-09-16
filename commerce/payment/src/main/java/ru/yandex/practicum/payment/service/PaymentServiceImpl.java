package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.payment.PaymentState;
import ru.practicum.interaction.api.exception.PaymentNotFoundException;
import ru.practicum.interaction.api.feign.contract.OrderContract;
import ru.practicum.interaction.api.feign.contract.StoreContract;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StoreContract shoppingStoreClient;
    private final OrderContract orderClient;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto orderDto) {
        BigDecimal total = BigDecimal.ZERO;
        Map<UUID, Integer> products = (Map<UUID, Integer>) orderDto.getProducts();
        Map<UUID, ProductDto> productsDto = shoppingStoreClient.findAllByIds(products.keySet());
        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            BigDecimal productPrice = productsDto.getOrDefault(productId,
                            ProductDto.builder().price(BigDecimal.ZERO).build())
                    .getPrice();

            BigDecimal lineTotal = productPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);
        }

        log.info("total cost {}: for order {}", orderDto.getOrderId(), total);
        return total;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto orderDto) {
        BigDecimal productTotal = productCost(orderDto);
        BigDecimal deliveryPrice = orderDto.getDeliveryPrice();

        BigDecimal vat = productTotal.multiply(BigDecimal.valueOf(0.1));

        BigDecimal total = productTotal.add(vat).add(deliveryPrice);

        log.info("total cost {}:  for order {}", orderDto.getOrderId(), total);
        return total;
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto orderDto) {
        BigDecimal productTotal = productCost(orderDto);

        BigDecimal deliveryTotal = orderDto.getDeliveryPrice();

        BigDecimal totalPayment = getTotalCost(orderDto);

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .productTotal(productTotal)
                .deliveryTotal(deliveryTotal)
                .totalPayment(totalPayment)
                .state(PaymentState.PENDING)
                .build();

        paymentRepository.save(payment);

        log.info("payment id {} for order {}", payment.getPaymentId(), orderDto.getOrderId());

        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment " + paymentId));

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.completeOrder(payment.getOrderId());
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment " + paymentId));

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.failPayment(payment.getOrderId());
    }
}