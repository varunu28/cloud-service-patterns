package com.varunu28.orderpickupservice.service;

import com.varunu28.orderpickupservice.model.Pickup;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderPickupService {

    private final Map<UUID, Pickup> pickups;

    public OrderPickupService() {
        pickups = new HashMap<>();
    }

    public Pickup getPickupById(UUID id) {
        if (!pickups.containsKey(id)) {
            throw new NoSuchElementException("Pickup with id " + id + " not found");
        }
        Pickup pickup = pickups.get(id);
        if (pickup.isCancelled()) {
            throw new NoSuchElementException("Pickup with id " + id + " has been cancelled");
        }
        return pickup;
    }

    public Pickup createPickup(UUID assigneeId, UUID orderId, String description) {
        ZonedDateTime now = ZonedDateTime.now();
        Pickup pickup = new Pickup(UUID.randomUUID(), description, orderId, assigneeId, now, now);
        pickups.put(pickup.getId(), pickup);
        return pickup;
    }

    public Pickup updatePickupById(UUID id, String description, Boolean completed) {
        if (!pickups.containsKey(id)) {
            throw new NoSuchElementException("Pickup with id " + id + " not found");
        }
        Pickup pickup = pickups.get(id);
        if (description != null) {
            pickup.setDescription(description);
        }
        if (completed != null && completed) {
            pickup.markAsCompleted();
        }
        return pickup;
    }

    public Pickup cancelPickup(UUID id) {
        if (!pickups.containsKey(id)) {
            throw new NoSuchElementException("Pickup with id " + id + " not found");
        }
        Pickup pickup = pickups.get(id);
        pickup.markAsCancelled();
        return pickup;
    }
}
