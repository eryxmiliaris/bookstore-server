package com.vb.bookstore.services;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.OrderItem;
import com.vb.bookstore.entities.Review;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.repositories.OrderItemRepository;
import com.vb.bookstore.repositories.ReviewRepository;
import com.vb.bookstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    public void updateRecommendations() {
        Map<User, HashMap<Book, BigDecimal>> data = new HashMap<>();
        List<User> users = userRepository.findAll();
        Set<Book> ratedBookList = new HashSet<>();
        for (User user : users) {
            List<Review> reviews = reviewRepository.findByUser(user);
            HashMap<Book, BigDecimal> userReviews = new HashMap<>();
            for (Review review : reviews) {
                userReviews.put(review.getBook(), review.getRating());
                ratedBookList.add(review.getBook());
            }
            data.put(user, userReviews);
        }

        Map<Book, Map<Book, BigDecimal>> diff = new HashMap<>();
        Map<Book, Map<Book, Integer>> freq = new HashMap<>();

        Map<User, HashMap<Book, BigDecimal>> outputData = new HashMap<>();

        for (HashMap<Book, BigDecimal> user : data.values()) {
            for (Map.Entry<Book, BigDecimal> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }
                for (Map.Entry<Book, BigDecimal> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey());
                    }
                    BigDecimal oldDiff = BigDecimal.ZERO;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey());
                    }
                    BigDecimal observedDiff = e.getValue().subtract(e2.getValue());
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff.add(observedDiff));
                }
            }
            for (Book j : diff.keySet()) {
                for (Book i : diff.get(j).keySet()) {
                    BigDecimal oldValue = diff.get(j).get(i);
                    int count = freq.get(j).get(i);
                    diff.get(j).put(i, oldValue.divide(BigDecimal.valueOf(count), RoundingMode.HALF_EVEN));
                }
            }

            HashMap<Book, BigDecimal> uPred = new HashMap<>();
            HashMap<Book, Integer> uFreq = new HashMap<>();
            for (Book j : diff.keySet()) {
                uFreq.put(j, 0);
                uPred.put(j, BigDecimal.ZERO);
            }
            for (Map.Entry<User, HashMap<Book, BigDecimal>> e : data.entrySet()) {
                for (Book j : e.getValue().keySet()) {
                    for (Book k : diff.keySet()) {
                        try {
                            BigDecimal predictedValue = diff.get(k).get(j).add(e.getValue().get(j));
                            BigDecimal finalValue = predictedValue.multiply(BigDecimal.valueOf(freq.get(k).get(j)));
                            uPred.put(k, uPred.get(k).add(finalValue));
                            uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                        } catch (NullPointerException e1) {
                        }
                    }
                }
                HashMap<Book, BigDecimal> clean = new HashMap<>();
                for (Book j : uPred.keySet()) {
                    if (uFreq.get(j) > 0) {
                        clean.put(j, uPred.get(j).divide(BigDecimal.valueOf(uFreq.get(j)), RoundingMode.HALF_EVEN));
                    }
                }
                for (Book j : ratedBookList) {
                    if (e.getValue().containsKey(j)) {
                        clean.put(j, e.getValue().get(j));
                    } else if (!clean.containsKey(j)) {
                        clean.put(j, BigDecimal.valueOf(-1));
                    }
                }
                outputData.put(e.getKey(), clean);
            }
        }

        for (Map.Entry<User, HashMap<Book, BigDecimal>> userEntry : outputData.entrySet()) {
            User user = userEntry.getKey();
            List<OrderItem> orderItems = orderItemRepository.findByOrder_User(user);
            Set<Book> ownedBooks = new HashSet<>();
            orderItems.forEach((item) -> ownedBooks.add(item.getBook()));

            HashMap<Book, BigDecimal> bookMap = userEntry.getValue();

            List<Book> recommendedBooks = bookMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .filter((entry) -> !ownedBooks.contains(entry.getKey()))
                    .map(Map.Entry::getKey)
                    .limit(50)
                    .collect(Collectors.toList());

            user.setRecommendedBooks(recommendedBooks);
            userRepository.save(user);
        }
    }
}
