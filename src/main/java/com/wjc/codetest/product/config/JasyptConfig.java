package com.wjc.codetest.product.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 문제: 민감 정보의 평문 노출 (보안/설계)
 * 원인:
 * - properties 내에 민감정보가 평문으로 기록되어 바이너리 및 Git 형상 관리 시 정보 유출 위험이 있습니다.
 * - 암호화된 프로퍼티를 자동으로 복호화하는 로직이 부재합니다.
 * 개선안:
 * - 대안: Jasypt 라이브러리를 통한 프로퍼티 암호화 및 외부 주입 전략 선택이 필요합니다.
 * - 선택 근거:
 *   - Spring Boot와의 높은 호환성(@EnableEncryptableProperties), ENC() 포맷을 통한 직관적인 암호문 관리,
 *   - 런타임에 자동 복호화되어 기존 코드 수정이 최소화됩니다.
 * - 트레이드오프:
 *   - 초기 빌드 시 복호화 과정으로 인한 속도가 소폭 증가하나, 보안성 강화 이점이 매우 중요합니다.
 *   - 마스터 키를 주입하기 위한 방안을 모색하고 적용해야 합니다.
 * 검증:
 * - Unit : JasyptConfigTests.java 실행으로 확인합니다.
 * - Integration : 다양한 방식의 마스터키 주입을 고려합니다.
 *
 * 참고 자료: https://www.baeldung.com/spring-boot-jasypt
 *
 * TODO
 * @message : config.setKeyObtentionIterations("777"); 보안성능 벤치마킹이 필요합니다.
 * @message : config.setPoolSize("1"); CPU 코어 활용률 검토가 필요합니다.
 */

//다음 주소를 참고하여 확장할 수 있도록 합니다
//https://www.baeldung.com/spring-boot-jasypt
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    @Bean("stringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        //마스터 키 설정
        config.setPassword("Seek a method to assign the master key !!");

        //암호화 알고리즘 설정
        config.setAlgorithm("PBEWithMD5AndDES"); // <-
        config.setKeyObtentionIterations("777"); // <-
        config.setPoolSize("1"); // <-
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        return encryptor;
    }
}