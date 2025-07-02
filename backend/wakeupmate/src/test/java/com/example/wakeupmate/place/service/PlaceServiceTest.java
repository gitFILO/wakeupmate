package com.example.wakeupmate.place.service;

import com.example.wakeupmate.place.domain.Place;
import com.example.wakeupmate.place.repository.PlaceRepository;
import com.example.wakeupmate.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceService placeService;

    @Test
    @DisplayName("사용자 ID로 장소 목록을 조회할 수 있다.")
    void findPlacesByUserId() {
        // given
        User user = createTestUser();
        Place place1 = createTestPlace("스타벅스", user, new BigDecimal("37.5665"), new BigDecimal("126.9780"));
        Place place2 = createTestPlace("카페베네", user, new BigDecimal("37.5663"), new BigDecimal("126.9779"));
        
        List<Place> places = List.of(place1, place2);

        when(placeRepository.findByUserId(anyLong())).thenReturn(places);

        // when
        List<Place> result = placeRepository.findByUserId(user.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("스타벅스");
        assertThat(result.get(1).getName()).isEqualTo("카페베네");
        verify(placeRepository).findByUserId(anyLong());
    }

    @Test
    @DisplayName("새로운 장소를 저장할 수 있다.")
    void savePlace() {
        // given
        User user = createTestUser();
        Place place = createTestPlace("새로운 카페", user, new BigDecimal("37.5665"), new BigDecimal("126.9780"));

        when(placeRepository.save(any(Place.class))).thenReturn(place);

        // when
        Place savedPlace = placeRepository.save(place);

        // then
        assertThat(savedPlace.getName()).isEqualTo("새로운 카페");
        assertThat(savedPlace.getUser()).isEqualTo(user);
        verify(placeRepository).save(any(Place.class));
    }

    private User createTestUser() {
        User user = User.builder()
                .socialLoginId("kakao_123456789")
                .username("테스트사용자")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .role("ROLE_USER")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Place createTestPlace(String name, User user, BigDecimal latitude, BigDecimal longitude) {
        Place place = new Place(name, user, latitude, longitude);
        return place;
    }
} 