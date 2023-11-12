package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

   // @ManyToOne
    //@JoinColumn(name = "users.id")
    @Column(name = "owner_id", nullable = false)
    private Long owner;

    // @ManyToOne
    //@JoinColumn(name = "requests.id")
    @Column(name = "request_id")
    private Long request;
}
