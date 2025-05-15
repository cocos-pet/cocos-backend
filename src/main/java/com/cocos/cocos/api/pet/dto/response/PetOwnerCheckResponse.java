package com.cocos.cocos.api.pet.dto.response;

public record PetOwnerCheckResponse(
        boolean isPetOwner
) {
    public static PetOwnerCheckResponse of(final boolean isPetOwner) {
        return new PetOwnerCheckResponse(isPetOwner);
    }
}
