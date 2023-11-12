package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@Builder
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    /*@ManyToOne
    @JoinColumn(name = "users.id")*/
    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;

}
