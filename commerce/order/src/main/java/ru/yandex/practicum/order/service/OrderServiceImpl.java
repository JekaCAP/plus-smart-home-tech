package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.order.ProductReturnRequest;
import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductForOrderFromShoppingCartRequest;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.enums.delivery.DeliveryState;
import ru.practicum.interaction.api.enums.order.OrderState;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.feign.contract.DeliveryContract;
import ru.practicum.interaction.api.feign.contract.PaymentContract;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;
import ru.yandex.practicum.order.mapper.AddressMapper;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Address;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseContract warehouseClient;
    private final DeliveryContract deliveryClient;
    private final PaymentContract paymentClient;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        List<Order> orders = orderRepository.findByUsername(username);

        return orders.stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("Создание нового заказа");

        UUID shoppingCartId = createNewOrderRequest.getShoppingCart().getShoppingCartId();

        Map<UUID, Integer> products = createNewOrderRequest.getShoppingCart().getProducts().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().intValue()
                ));

        Order order = Order.builder()
                .shoppingCartId(shoppingCartId)
                .username(createNewOrderRequest.getUsername())
                .products(products)
                .state(OrderState.NEW)
                .build();

        Order savedOrder = orderRepository.save(order);

        BookedProductsDto bookedProduct = warehouseClient.assembleOrder(
                new AssemblyProductForOrderFromShoppingCartRequest(shoppingCartId, savedOrder.getOrderId()));

        savedOrder.setDeliveryWeight(bookedProduct.getDeliveryWeight());
        savedOrder.setDeliveryVolume(bookedProduct.getDeliveryVolume());
        savedOrder.setFragile(bookedProduct.getFragile());

        AddressDto fromAddressDto = warehouseClient.getWarehouseAddress();
        Address toAddress = addressMapper.fromAddressDto(createNewOrderRequest.getAddress());
        savedOrder.setFromAddress(addressMapper.fromAddressDto(fromAddressDto));
        savedOrder.setToAddress(toAddress);

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(savedOrder.getOrderId())
                .fromAddress(fromAddressDto)
                .toAddress(createNewOrderRequest.getAddress())
                .status(DeliveryState.CREATED)
                .build();

        DeliveryDto createdDelivery = deliveryClient.planDelivery(deliveryDto);
        savedOrder.setDeliveryId(createdDelivery.getDeliveryId());

        BigDecimal productPrice = paymentClient.productCost(orderMapper.toOrderDto(savedOrder));
        savedOrder.setProductPrice(productPrice);

        BigDecimal deliveryPrice = deliveryClient.deliveryCost(orderMapper.toOrderDto(savedOrder));
        savedOrder.setDeliveryPrice(deliveryPrice);

        BigDecimal totalPrice = paymentClient.getTotalCost(orderMapper.toOrderDto(savedOrder));
        savedOrder.setTotalPrice(totalPrice);

        orderRepository.save(savedOrder);

        log.info("Заказ создан: {}", savedOrder.getOrderId());

        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Инициация оплаты для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        PaymentDto paymentDto = paymentClient.payment(orderMapper.toOrderDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.PAID);

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Обработка неудачной оплаты для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Инициация доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Обработка неудачной доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto completed(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчет общей стоимости для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        BigDecimal totalPrice = paymentClient.getTotalCost(orderMapper.toOrderDto(order));
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчет стоимости доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        BigDecimal deliveryPrice = deliveryClient.deliveryCost(orderMapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Обработка успешной сборки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Обработка неудачной сборки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("Обработка возврата для заказа: {}", productReturnRequest.getOrderId());
        Order order = getOrderById(productReturnRequest.getOrderId());

        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден: " + orderId));
    }
}