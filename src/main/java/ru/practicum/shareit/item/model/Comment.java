package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

   /* @ManyToOne
    @JoinColumn(name = "items.id")*/
    @Column(name = "item_id ", nullable = false)
    private Long itemId;

    /* @ManyToOne
    @JoinColumn(name = "users.id")*/
    @Column(name = "author_id ", nullable = false)
    private Long authorId;
}
