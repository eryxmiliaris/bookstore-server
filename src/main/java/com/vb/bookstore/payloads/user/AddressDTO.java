package com.vb.bookstore.payloads.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9\\s'-]+$", message = "Invalid address name")
    private String name;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*([- ][A-Z][a-z]*)*$", message = "Invalid name")
    private String userName;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*([- ][A-Z][a-z]*)*$", message = "Invalid surname")
    private String userSurname;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*([- ][A-Z][a-z]*)*$", message = "Invalid city name")
    private String city;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*(?:[ -][A-Z][a-z]*)*\\s\\d+[A-Za-z]*$", message = "Invalid street address")
    private String street;

    @NotBlank
    @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$", message = "Invalid postal code (valid example: 20-501)")
    private String postalCode;

    @NotBlank
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Invalid phone number")
    private String phoneNumber;
}
