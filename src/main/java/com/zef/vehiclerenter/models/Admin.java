package com.zef.vehiclerenter.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Admin {
    private final UUID id;
    private final String email;
    private final String name;

    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    private OffsetDateTime deletedAt = null;

    public Admin(
            UUID id,
            String email,
            String name
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public Admin(
            UUID id,
            String email,
            String name,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(id, email, name);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Admin(
            UUID id,
            String email,
            String name,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime deletedAt
    ) {
        this(id, email, name, createdAt, updatedAt);
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }
}
