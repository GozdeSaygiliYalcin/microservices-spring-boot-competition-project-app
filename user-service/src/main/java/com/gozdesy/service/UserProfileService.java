package com.gozdesy.service;

import com.gozdesy.dto.request.EditProfileRequestDto;
import com.gozdesy.dto.request.NewUserCreateDto;
import com.gozdesy.mapper.IUserProfileMapper;
import com.gozdesy.repository.IUserProfileRepository;
import com.gozdesy.repository.entity.UserProfile;
import com.gozdesy.utility.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService extends ServiceManager<UserProfile, Long> {

    private final IUserProfileRepository userProfileRepository;
    @Autowired
    private CacheManager cacheManager;

    public UserProfileService(IUserProfileRepository userProfileRepository) {
        super(userProfileRepository);
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile createUserProfile(NewUserCreateDto dto) {
        return save(UserProfile.builder()
                .authId(dto.getAuthId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .build());
    }

    public Boolean updateUserProfile(EditProfileRequestDto dto, Long authId) {
        UserProfile userProfile = IUserProfileMapper.INSTANCE.toUserProfile(dto);
        Optional<UserProfile> optionalUserProfile = userProfileRepository.findOptionalByAuthId(authId);
        if (optionalUserProfile.isEmpty())
            return false;
        try {
            userProfile.setId(optionalUserProfile.get().getId());
            update(userProfile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public List<UserProfile> findAllPage(int currentPage, int pageSize) {
//        Pageable pageable = PageRequest.of(currentPage, pageSize);
//        return userProfileRepository.findAll(pageable);
//    }


    public Page<UserProfile> findAllPage(int currentPage, int pageSize, String sortParameter, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortParameter);
        Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
        return userProfileRepository.findAll(pageable);
    }

    public Slice<UserProfile> findAllSlice(int currentPage, int pageSize,
                                           String sortParameter,
                                           String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortParameter);
        Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
        return userProfileRepository.findAll(pageable);
    }

    public void clearCache(String key, String parameter) {
        cacheManager.getCache(key).evict(parameter);
    }

    /**
     * [Method Adı] :: [Değer] -> id
     * Clear ->
     */
    @Cacheable(value = "userprofile_getall")
    public List<UserProfile> getAllCache() {
        return userProfileRepository.findAll();
    }


    public List<UserProfile> getById(Long id) {
        return userProfileRepository.findAll();
    }
}
