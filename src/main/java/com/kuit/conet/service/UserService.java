/*
package com.kuit.conet.service;

import com.kuit.conet.common.exception.UserException;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.dto.web.request.member.NameRequestDTO;
import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.response.member.MemberResponseDTO;
import com.kuit.conet.jpa.domain.storage.StorageDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final StorageService storageService;

    public void userDelete(Long userId) {
        // S3 에서 프로필 이미지 객체 삭제
        String imgUrl = userDao.getUserImgUrl(userId);
        String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
        if (!userDao.isDefaultImage(userId)) {
            storageService.deleteImage(deleteFileName);
        }

        userDao.deleteUser(userId);
    }

    public MemberResponseDTO getUser(Long userId) {
        isExistUser(userId);

        // S3에 없는 객체에 대한 유효성 검사
        String imgUrl = userDao.getUserImgUrl(userId);
        String fileName = storageService.getFileNameFromUrl(imgUrl);
        if (!storageService.isExistImage(fileName)) {
            log.warn("S3 버킷에 존재하지 않는 이미지입니다. 기본 이미지로 변경하겠습니다.");
            userDao.setImageUrlDefault(userId);
        }

        return userDao.getUser(userId);
    }

    public StorageImgResponseDTO updateImg(Long userId, MultipartFile file) {
        isExistUser(userId);

        // 저장할 파일명 만들기
        // 받은 파일이 이미지 타입이 아닌 경우에 대한 유효성 검사 진행
        String fileName = storageService.getFileName(file, StorageDomain.USER);

        */
/*
        유저의 프로필 이미지가 기본 프로필 이미지인지 확인 -> 기본 이미지가 아니면 기존 이미지를 S3에서 이미지 삭제
        S3 버킷에 존재하지 않는 객체인 경우 삭제를 생략 *//*

        String imgUrl = userDao.getUserImgUrl(userId);
        String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
        if (!userDao.isDefaultImage(userId)) {
            storageService.deleteImage(deleteFileName);
        }

        // 새로운 이미지 S3에 업로드
        imgUrl = storageService.uploadToS3(file, fileName);

        return userDao.updateImg(userId, imgUrl);
    }

    public void updateName(Long userId, NameRequestDTO nameRequest) {
        isExistUser(userId);

        userDao.updateName(userId, nameRequest.getName());
    }

    private void isExistUser(Long userId) {
        // 존재하는 유저인지 검사
        if (!userDao.isExistUser(userId)) {
            throw new UserException(NOT_FOUND_USER);
        }
    }
}*/
