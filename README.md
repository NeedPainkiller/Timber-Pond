# Timber Pond

<p align="center">
  <a href="https://github.com/NeedPainkiller/Timber" target="blank"><img src="./img/timber-logo.svg" width="200" alt="Nest Logo" /></a>
</p>


> **_NOTE:_**  한국어 설명은 아래에 있습니다.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Description
File Management for [Timber](https://github.com/NeedPainkiller/Timber) Framework

## Requirements
- <img src="https://img.shields.io/badge/Java-67493A?style=flat-square&logo=OpenJDK&logoColor=white"/>
- <img src="https://img.shields.io/badge/Quarkus-4695EB?style=flat-square&logo=quarkus&logoColor=white"/>
- <img src="https://img.shields.io/badge/Panache-1E404E?style=flat-square&logo=OpenJDK&logoColor=white"/>
- <img src="https://img.shields.io/badge/Apache Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white"/>
- <img src="https://img.shields.io/badge/ReactiveX-B7178C?style=flat-square&logo=ReactiveX&logoColor=white"/>
- <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/>



## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application
### Packaging
The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

## if else) Packaging as an _über-jar_
If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/pond-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

---
# Timber Pond

<p align="center">
  <a href="https://github.com/NeedPainkiller/Timber" target="blank"><img src="./img/timber-logo.svg" width="200" alt="Nest Logo" /></a>
</p>

이 프로젝트는 Quarkus, 경량 고속 자바 프레임워크로 개발되었습니다.

Quarkus에 대해 더 알아보려면 웹사이트를 방문하세요: https://quarkus.io/ .

## Description | 이 프로젝트는....
Timber Pond 프로젝트는 [Timber](https://github.com/NeedPainkiller/Timber) 를 위한 파일 관리 서비스입니다.

프로젝트 Timber 에 관해서는 [Timber Repository](https://github.com/NeedPainkiller/Timber) 를 참조해 주세요

## Requirements | 요구사항
- <img src="https://img.shields.io/badge/Java-67493A?style=flat-square&logo=OpenJDK&logoColor=white"/>
- <img src="https://img.shields.io/badge/Quarkus-4695EB?style=flat-square&logo=quarkus&logoColor=white"/>
- <img src="https://img.shields.io/badge/Panache-1E404E?style=flat-square&logo=OpenJDK&logoColor=white"/>
- <img src="https://img.shields.io/badge/Apache Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white"/>
- <img src="https://img.shields.io/badge/ReactiveX-B7178C?style=flat-square&logo=ReactiveX&logoColor=white"/>
- <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/>



## 개발 모드로 실행

실시간 코딩을 가능하게 하는 개발 모드로 애플리케이션을 실행할 수 있습니다:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus는 이제 개발 UI를 제공하며 개발 모드에서만 http://localhost:8080/q/dev/ 사용해야하며, production 모드에서는 비활성화 해야 합니다.
 
## 패키징 및 애플리케이션 실행
### 패키징
애플리케이션은 다음과 같이 패키징 할 수 있습니다:
```shell script
./gradlew build
```
이 명령은 `build/quarkus-app/` 디렉토리에 `quarkus-run.jar` 파일을 생성합니다.

의존성이 `build/quarkus-app/lib/` 디렉토리로 복사되므로 _über-jar_가 아님을 유의하십시오.

애플리케이션은 이제 `java -jar build/quarkus-app/quarkus-run.jar`를 사용하여 실행할 수 있습니다.


## 만약) 패키징을 _über-jar_로 하고 싶다면
다음 명령을 실행하면 _über-jar_를 빌드할 수 있습니다:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

_über-jar_로 패키징된 애플리케이션은 `java -jar build/*-runner.jar`를 사용하여 실행할 수 있습니다.

## 네이티브 실행 파일 생성
다음과 같이 네이티브 실행 파일을 생성할 수 있습니다:
```shell script
./gradlew build -Dquarkus.package.type=native
```

또는 GraalVM이 설치되어 있지 않은 경우 다음 명령을 사용하여 컨테이너에서 네이티브 실행 파일을 빌드할 수 있습니다:
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

그런 다음 `./build/pond-1.0-SNAPSHOT-runner`로 네이티브 실행 파일을 실행할 수 있습니다.

네이티브 실행 파일을 빌드하는 방법에 대해 자세히 알아보려면 https://quarkus.io/guides/gradle-tooling 를 참조하십시오.

## Stay in touch
<p>
  <a href="https://home.needpainkiller.xyz/" target="_blank"><img src="https://img.shields.io/badge/Home-EF3346?style=flat-square&logo=googlehome&logoColor=white"/></a>
  <a href="https://blog.needpainkiller.xyz/" target="_blank"><img src="https://img.shields.io/badge/Blog-15171A?style=flat-square&logo=Ghost&logoColor=white"/></a>
  <a href="mailto:kam6512@gmail.com" target="_blank"><img src="https://img.shields.io/badge/kam6512@gmail.com-EA4335?style=flat-square&logo=Gmail&logoColor=white"/></a>
  <a href="mailto:needpainkiller6512@gmail.com" target="_blank"><img src="https://img.shields.io/badge/needpainkiller6512@gmail.com-EA4335?style=flat-square&logo=Gmail&logoColor=white"/></a>
</p>

- Author - [NeedPainkiller](https://home.needpainkiller.xyz/)
- Blog - [https://blog.needpainkiller.xyz](https://blog.needpainkiller.xyz/)
- Github - [@PainKiller](https://github.com/NeedPainkiller)

## License

Timber-Pond is [MIT licensed](LICENSE).
