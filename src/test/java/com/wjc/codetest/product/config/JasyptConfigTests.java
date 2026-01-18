package com.wjc.codetest.product.config;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//무결성검증
@SpringBootTest
class JasyptConfigTests {

    @Autowired
    @Qualifier("stringEncryptor") // 설정 클래스에서 정의한 빈 이름
    private StringEncryptor encryptor;

    @Test
    @DisplayName("민감 정보 암복호화 테스트: 평문이 암호화 후 다시 원문으로 복호화되어야 한다")
    void jasypt_encryption_decryption_test() {
        // Given: 보호해야 할 민감 정보 (예: DB 패스워드)
        String plainText = "my-secret-password-1234";

        // When: 암호화 진행
        String encryptedText = encryptor.encrypt(plainText);
        String decryptedText = encryptor.decrypt(encryptedText);

        // Then: 검증
        System.out.println("평문: " + plainText);
        System.out.println("Encrypted Text: ENC(" + encryptedText + ")");
        System.out.println("Decrypted Text: " + decryptedText);

        assertThat(encryptedText).isNotEqualTo(plainText); // 암호화 결과가 평문과 달라야 함
        assertThat(decryptedText).isEqualTo(plainText);    // 복호화 결과가 평문과 같아야 함
    }
}