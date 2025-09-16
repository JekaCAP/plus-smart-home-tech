package ru.yandex.practicum.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.yandex.practicum.delivery.model.Delivery;

import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    default Delivery findByDeliveryId(UUID deliveryId) {
        return findById(deliveryId).orElseThrow(
                () -> new NoOrderFoundException(String.format("Delivery with id " + deliveryId + " not found"))
        );
    }
}