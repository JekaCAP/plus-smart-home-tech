package ru.yandex.practicum.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.delivery.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}