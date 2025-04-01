package com.assignment.implmentation;

import com.assignment.DTO.PromoCodeDTO;
import com.assignment.entites.Product;
import com.assignment.entites.PromoCode;
import com.assignment.exception.InvalidDataException;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.ProductRepository;
import com.assignment.repository.PromoCodeRepository;
import com.assignment.services.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of PromoCodeService interface.
 * <p>
 * This class provides services for managing promo codes,
 * including creating promo codes and automatically deactivating expired ones.
 * </p>
 */
@Service
public class PromoCodeServiceImp implements PromoCodeService {

    @Autowired
    PromoCodeRepository promoCodeRepository;

    @Autowired
    ProductRepository productRepository;

    /**
     * Creates a new promo code based on the provided DTO.
     * <p>
     * Validates the input data and ensures that the associated product exists
     * if the promo code is product-specific. If validation errors occur,
     * a map of error messages is returned.
     * </p>
     *
     * @param promoCodeDTO  The DTO containing promo code details.
     * @param bindingResult The result of input validation.
     * @return ResponseEntity containing a success message or validation errors.
     */
    @Override
    public ResponseEntity<Object> createPromoCode(PromoCodeDTO promoCodeDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }


        LocalDate expiryDateParsed = LocalDate.parse(promoCodeDTO.getExpiryDate());
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(promoCodeDTO.getCode());
        promoCode.setDiscountPercentage(promoCodeDTO.getDiscountPercentage());
        promoCode.setMinOrderAmount(promoCodeDTO.getMinOrderAmount());
        promoCode.setProductSpecific(promoCodeDTO.getIsProductSpecific());
        promoCode.setActive(promoCodeDTO.getIsActive());
        promoCode.setExpiryDate(expiryDateParsed);

        // Validate product if the promo code is product-specific
        if (promoCodeDTO.getIsProductSpecific()) {
            if (promoCodeDTO.getProductId() == null) {
                throw new InvalidDataException("Product ID is required when the promo code is product-specific.");
            }

            // If the promo code is product-specific, set the product
            Product product = productRepository.findById(promoCodeDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + promoCodeDTO.getProductId() + " does not exist."));
            promoCode.setProduct(product);
        }

        // Save the promo code to the database
        promoCodeRepository.save(promoCode);
        return ResponseEntity.ok(Map.of("message", "Promo code created successfully"));
    }

    /**
     * Automatically deactivates expired promo codes at a scheduled time.
     * <p>
     * This method runs at 5:30 PM every day and checks for promo codes
     * whose expiry date has passed but are still active. Any such promo codes
     * are deactivated and updated in the database.
     * </p>
     */
    @Scheduled(cron = "0 30 17 * * ?")
    @Override
    public void deactivateExpiredPromoCodes() {
        // Fetch all promo codes that have expired and are still active
        List<PromoCode> expiredPromoCodes = promoCodeRepository.findByExpiryDateBeforeAndIsActiveTrue(LocalDate.now());

        // Deactivate each expired promo code
        if (!expiredPromoCodes.isEmpty()) {
            for (PromoCode promoCode : expiredPromoCodes) {
                promoCode.setActive(false);  // Deactivate the expired promo code
                promoCodeRepository.save(promoCode);  // Save the updated promo code
                System.out.println("Deactivated expired promo code: " + promoCode.getCode());
            }
        }
    }

    @Override
    public ResponseEntity<Object> findAllPromoCode() {
        List<PromoCode> promoCodes = promoCodeRepository.findAll();

        if (promoCodes.isEmpty()) {
            throw new ResourceNotFoundException("No promo codes found.");
        }

        // ✅ Updated to fetch product name if applicable
        List<PromoCodeDTO> promoCodeDTOList = promoCodes.stream().map(promoCode -> {
            PromoCodeDTO dto = new PromoCodeDTO();
            dto.setCode(promoCode.getCode());
            dto.setDiscountPercentage(promoCode.getDiscountPercentage());
            dto.setMinOrderAmount(promoCode.getMinOrderAmount());
            dto.setIsProductSpecific(promoCode.getProductSpecific());

            // ✅ Get product name if the promo code is product-specific
            if (promoCode.getProduct() != null && promoCode.getProductSpecific()) {
                dto.setProductName(promoCode.getProduct().getName()); // Get product name
            } else {
                dto.setProductName("Applicable to all products");
            }

            dto.setIsActive(promoCode.getActive());
            dto.setExpiryDate(promoCode.getExpiryDate().toString());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("promoCodes", promoCodeDTOList));
    }
}
