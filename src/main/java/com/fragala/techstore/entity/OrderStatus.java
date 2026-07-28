package com.fragala.techstore.entity;

/**
 * Enumeration of the possible lifecycle states for an order.
 *
 * <p>This enum exists to constrain order status values to a known set of business states rather
 * than allowing arbitrary strings. That makes the model safer and easier to reason about.
 *
 * <p>In the architecture, it supports the {@link Order} entity by modeling workflow state in a
 * type-safe way.
 *
 * <p>It is used whenever the application needs to track or inspect the current processing stage
 * of an order.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
