package com.vb.bookstore.tasks;

import com.vb.bookstore.config.AppConstants;
import com.vb.bookstore.entities.*;
import com.vb.bookstore.repositories.*;
import com.vb.bookstore.services.BookService;
import com.vb.bookstore.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final BookService bookService;
    private final RecommendationService recommendationService;
    private final PromoCodeRepository promoCodeRepository;
    private final UserRepository userRepository;
    private final EbookRepository ebookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final AudiobookRepository audiobookRepository;
    private final PaperBookRepository paperBookRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateRecommendations() {
        recommendationService.updateRecommendations();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateBooksPopularity() {
        bookService.updatePopularityScore();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updatePromoCodes() {
        List<PromoCode> promoCodesToUpdate = promoCodeRepository.findByEndDateBeforeAndIsActiveTrue(LocalDateTime.now());

        promoCodesToUpdate.forEach(promoCode -> promoCode.setIsActive(false));
        promoCodeRepository.saveAll(promoCodesToUpdate);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateSubscriptions() {
        List<User> usersToUpdate = userRepository.findByActiveSubscriptionEndDateLessThan(LocalDate.now());

        usersToUpdate.forEach(user -> {
            user.setHasActiveSubscription(false);
            user.setActiveSubscriptionEndDate(null);
        });

        userRepository.saveAll(usersToUpdate);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePaperBookDiscounts() {
        List<PaperBook> paperBooksToUpdate = paperBookRepository.findByDiscountEndDateBefore(LocalDateTime.now());

        List<CartItem> cartItems = cartItemRepository.findByPaperBookIdIn(paperBooksToUpdate.stream().map(PaperBook::getId).toList());

        Set<Cart> carts = new HashSet<>();
        cartItems.forEach(cartItem -> carts.add(cartItem.getCart()));

        paperBooksToUpdate.forEach(paperBook -> {
            paperBook.setHasDiscount(false);
            paperBook.setPriceWithDiscount(paperBook.getPrice());
            paperBook.setDiscountPercentage(null);
            paperBook.setDiscountAmount(null);
            paperBook.setDiscountEndDate(null);
        });

        cartItems.forEach(cartItem -> {
            cartItem.setHasDiscount(false);
            cartItem.setPriceWithDiscount(cartItem.getPrice());
            cartItem.setTotalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        });

        carts.forEach(Cart::updateTotalPrice);

        paperBookRepository.saveAll(paperBooksToUpdate);
        cartRepository.saveAll(carts);
        cartItemRepository.saveAll(cartItems);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateEbookDiscounts() {
        List<Ebook> ebooksToUpdate = ebookRepository.findByDiscountEndDateBefore(LocalDateTime.now());

        List<CartItem> cartItems = cartItemRepository.findByBookTypeAndBook_IdIn(AppConstants.EBOOK, ebooksToUpdate.stream().map(Ebook::getId).toList());

        Set<Cart> carts = new HashSet<>();
        cartItems.forEach(cartItem -> carts.add(cartItem.getCart()));

        ebooksToUpdate.forEach(ebook -> {
            ebook.setHasDiscount(false);
            ebook.setPriceWithDiscount(ebook.getPrice());
            ebook.setDiscountPercentage(null);
            ebook.setDiscountAmount(null);
            ebook.setDiscountEndDate(null);
        });

        cartItems.forEach(cartItem -> {
            cartItem.setHasDiscount(false);
            cartItem.setPriceWithDiscount(cartItem.getPrice());
            cartItem.setTotalPrice(cartItem.getPrice());
        });

        carts.forEach(Cart::updateTotalPrice);

        ebookRepository.saveAll(ebooksToUpdate);
        cartRepository.saveAll(carts);
        cartItemRepository.saveAll(cartItems);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateAudiobookDiscounts() {
        List<Audiobook> audiobooksToUpdate = audiobookRepository.findByDiscountEndDateBefore(LocalDateTime.now());

        List<CartItem> cartItems = cartItemRepository.findByBookTypeAndBook_IdIn(AppConstants.AUDIOBOOK, audiobooksToUpdate.stream().map(Audiobook::getId).toList());

        Set<Cart> carts = new HashSet<>();
        cartItems.forEach(cartItem -> carts.add(cartItem.getCart()));

        audiobooksToUpdate.forEach(audiobook -> {
            audiobook.setHasDiscount(false);
            audiobook.setPriceWithDiscount(audiobook.getPrice());
            audiobook.setDiscountPercentage(null);
            audiobook.setDiscountAmount(null);
            audiobook.setDiscountEndDate(null);
        });

        cartItems.forEach(cartItem -> {
            cartItem.setHasDiscount(false);
            cartItem.setPriceWithDiscount(cartItem.getPrice());
            cartItem.setTotalPrice(cartItem.getPrice());
        });

        carts.forEach(Cart::updateTotalPrice);

        audiobookRepository.saveAll(audiobooksToUpdate);
        cartRepository.saveAll(carts);
        cartItemRepository.saveAll(cartItems);
    }
}
