package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Data;


@Data
public class CommentDto {

    private Long id;

    private String contents;

    private User user;

    private Long townBookId;

    private Long parentId;
}