package com.ratingsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerOfferId implements Serializable {
    private Integer seller;
    private Integer game;
}
