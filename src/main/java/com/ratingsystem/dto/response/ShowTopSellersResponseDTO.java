package com.ratingsystem.dto.response;

public record ShowTopSellersResponseDTO (
        SellerDTO sellerDTO,
        Float avgRating
) {
}
