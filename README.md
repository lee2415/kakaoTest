# kakaoTest
카카오페이 경력 과제

# 개발 프레임워크 
구분|제품명|Version
--|--|--
|개발언어|Java|1.8
| DBMS | H2 |   
| FrameWork | Spring Boot | 2.1.3.RELEASE 

# 문제해결 전략
1. JWT 인증
- 재발급 요청 시 기존에 발급한 Token을 입력해야 하는데, 해당 Token이 만료될 경우 내부 정보 조회가 불가하여 만료될 경우에는 재 로그인으로 기준 설정

2. json return의 경우 모두 200 으로 return하고, return 같이 보내는 code값으로 판단하여 에러 처리 진행

3. api return형태를 맞추기 위해서 쿼리가 복잡해지고 모든 쿼리를 반영하는것보다는, 조회 후 return형식에 맞게 변경하도록 하는 방향이 효율적으로 판단되어 return을 위한 유틸을 생성하여 결과값에 반영

4. 정렬의 경우 문자열의 정렬이 필요하여, 조회 후 해당 문자열을 수치화 하여 각각 비교하여 정렬 진행.

5. 두가지 정렬의 경우 Comparator 사용 시 앞에서 정렬한 내용을 무시하고 재정렬을 진행하는 문제가 있어 수동으로 정렬 진행

6. 문자열 분석
- 실제 검색이 필요한 문자열에 대해 미리 정의 후 해당 문자열 기준으로 조회하도록 진행 함.
- 위치 정보는 실제로 해당 값을 구하기 어려워, 랜덤함수를 이용하여 초기 데이터 로딩 시 저장하여 사용하도록 진행
( 위치 정보가 데이터 저장 시 변경되기 때문에 데이터를 새로 넣을 경우 위치 데이터가 변경되어 결과값이 변경 됨 )
- 문자열에서 위치에 조회를 위해서 지역에 대한 정보를 csv로 만들어 해당 지역정보를 이용하여 검색 진행, csv에 없을 경우 검색이 안되는 문제점이 있음. 


# 빌드 및 실행 방법
### 빌드 방법
빌드는 Maven을 사용하여 빌드 진행
``` mvn clean package ```

위 결과로 생성된 파일 실행
``` java -jar target/kakaoTest-0.0.1-SNAPSHOT.jar ```

접속 URL : http://localhost:8080

# API 정의
API 입력은 모두 json 형태로 진행.

#### 공통 output sample
    정상일 경우 아래와 같이 공통 return 메시지와 나머지 정보를 같이 return 한다.
    {
        "code": "00",
        "message": ""
    }

    실패일 경우 code와 메시지를 아래와 같이 출력
    {
        "error": {
            "code": "ERROR.MEMBER",
            "message": "회원 가입을 위한 필수값이 부족합니다."
        }
    }

* * *

## 회원 가입 관련 API
1. **회원 가입**

구분|값
--|--
URL|/member/signup
method|POST
API 설명|userID와 userPass를 입력 받아 회원 가입을 진행하는 API 회원 가입 후 인증에 대한 Token을 헤더에 담아 발행해 준다. 

#### input example
    {
	    "userId":"leel2415",
	    "userPass":"12341234"
    }

#### output exampel
    {
        "code": "00",
        "message": ""
    }
#### output header 
    Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcGlBdXRoIjp0cnVlLCJleHAiOjE1NTQzNjc0OTcsInVzZXJJZCI6ImxlZWwyNDE1IiwiaWF0IjoxNTU0MzYzODk3fQ.I0fb4JSWeX6F5QzptoaN6Uq-vq1-uCHS_9SEQ8XwtUY
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Thu, 04 Apr 2019 07:44:57 GMT

* * *

2. **로그인**

구분|값
--|--
URL|/member/signin
method|POST
API 설명|userID와 userPass를 입력 받아 로그인 처리를 진행하는 API, 로그인 후 인증을 위한 Token을 헤더에 담아 return 함.

#### input example
    {
	    "userId":"leel2415",
	    "userPass":"12341234"
    }

#### output exampel
    {
        "code": "00",
        "message": ""
    }
#### output header 
    Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcGlBdXRoIjp0cnVlLCJleHAiOjE1NTQ2MjM3NzMsInVzZXJJZCI6ImxlZWwyNDE1IiwiaWF0IjoxNTU0NjIwMTczfQ.XbYF1pAjwPIbCHEB78vC2HSxrmg63wVH6LaP-Vp8fU4
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sun, 07 Apr 2019 06:56:14 GMT

* * *

3. **Token 재발급 요청 API**

구분|값
--|--
URL|/member/refresh
method|POST
API 설명|기존 토큰을 Header에 담아 요청 하게되면, 해당 Token에 대한 정보를 이용하여 Token 을 재발행, 만료된 토큰의 경우에는 로그인을 다시 해야함. 

#### input example
    별도의 input 불필요
    {
    }

#### input header example
    Authorization : Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcGlBdXRoIjp0cnVlLCJleHAiOjE1NTQ2MjM3NzMsInVzZXJJZCI6ImxlZWwyNDE1IiwiaWF0IjoxNTU0NjIwMTczfQ.XbYF1pAjwPIbCHEB78vC2HSxrmg63wVH6LaP-Vp8fU4
    Content-Type : application/json

#### output example
    {
        "code": "00",
        "message": ""
    }
#### output header example
    Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcGlBdXRoIjp0cnVlLCJleHAiOjE1NTQ2MjM5ODgsInVzZXJJZCI6ImxlZWwyNDE1IiwiaWF0IjoxNTU0NjIwMzg4fQ.C35xu6jK3JVSZpnj4uZQNUgDNB1gn_fDxpFW0vNfhmM
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sun, 07 Apr 2019 06:59:48 GMT


* * *
* * *

## 서비스 API

서비스 API는 위에 가입하여 발급되는 JWT Token을 사용해야지만 서비스가 가능하며, 발급한 Token을 Header에 추가하여 요청이 필요함.

Token에 대한 만료시간은 **1800s** 로 설정함. 

#### input header example
    Authorization : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcGlBdXRoIjp0cnVlLCJleHAiOjE1NTQ2MjM5ODgsInVzZXJJZCI6ImxlZWwyNDE1IiwiaWF0IjoxNTU0NjIwMzg4fQ.C35xu6jK3JVSZpnj4uZQNUgDNB1gn_fDxpFW0vNfhmM
    Content-Type : application/json

* * *

1. **데이터 파일에서 각 레코드를 데이터베이스에 저장 API**

구분|값
--|--
URL|/api/save
method|POST
API 설명|csv의 지자체 관련 정보 데이터 파일을 DB에 초기 데이터를 저장하는 API. 호출 시, csv 파일을 읽어와 DB에 저장한다.

#### input example
    {
    }

#### output example
    {
       "code": "00",
        "message": ""
    }

* * *

2. **지원하는 지자체 목록 검색 API**

구분|값
--|--
URL|/api/select
mehtod|GET
API 설명|현재 저장되어 있는 지자체 목록 정보를 조회하는 API, 저장되어 있는 모든 정보를 return 함

#### input example
    {
    }

#### output example
    [
        {
            "mgmt": "춘천지점",
            "rate": "3%~5%",
            "usage": "운전",
            "limit": "8억원 이내",
            "institute": "강원도",
            "region": "강원도",
            "reception": "강원도 소재 영업점",
            "target": "강원도 소재 중소기업으로서 강원도지사가 추천한 자"
        },
        {
            "mgmt": "거제지점",
            "rate": "2.5%~5.0%",
            "usage": "운전",
            "limit": "3억원 이내",
            "institute": "거제시, 경남신용보증재단",
            "region": "거제시",
            "reception": "거제시, 통영시에 소재영업점",
            "target": "거재시 소재 중소기업(소상공인 포함)으로서 거제시장이 추천한자"
        },
        ...
    ]

* * *

3. **지원하는 지자체명을 입력 받아 해당 지자체의 지원정보를 출력하는 API**

구분|값
--|--
URL|/api/select/region
mehtod|GET
API 설명|지자체명으로 해당 지자체 정보를 검색하는 API 

#### input example
    {
        "region":"강릉시"
    }

#### output example
    {
        "mgmt": "강릉지점",
        "rate": "3%",
        "usage": "운전",
        "limit": "추천금액 이내",
        "institute": "강릉시",
        "reception": "강릉시 소재 영업점",
        "region": "강릉시",
        "target": "강릉시 소재 중소기업으로서 강릉시장이 추천한 자"
    }

* * *

4. **지원하는 지자체 정보 수정기능 API**

구분|값
--|--
URL|/api/update
mehtod|PUT
API 설명|지자체 정보를 수정하는 API, 입력한 지자체명 기준으로 input한 데이터들을 수정한다. 

#### input example
    이차보전 비율 수정하는 예제
    {
        "region":"강릉시",
        "rate":"5%"
    }
#### output example
    {
        "mgmt": "강릉지점",
        "rate": "5%",
        "usage": "운전",
        "limit": "추천금액 이내",
        "institute": "강릉시",
        "region": "강릉시",
        "reception": "강릉시 소재 영업점",
        "target": "강릉시 소재 중소기업으로서 강릉시장이 추천한 자"
    }

* * *

5. **지원한도 컬럼에서 지원금액으로 내림차순 정렬(지원금액이 동일하면 이차보전 평균 비율이 적은 순서)하여 특정 개수만 출력하는 API**

구분|값
--|--
URL|/api/sort
mehtod|GET
API 설명|개수를 입력받아 해당 개수만큼 지원금액 내림차순 정렬로 정렬하여 지자체명을 List로 return 하는 API , 지원금액이 같을 경우 이차보전 평귤 비율이 적은 순서 ( 2% ~ 4% 일 경우 뒤의 큰 비율로 계산)로 정렬함

#### input example
    {
	    "k":"10"
    }

#### output example
    {
        "regionList": [
            "강릉시",
            "농림축산식품부",
            "한국방송통신전파진흥원",
            "근로복지공단",
            "한국장애인고용공단",
            "보령시",
            "양산시",
            "안양상공회의소",
            "포천시",
            "울산동구"
        ]
    }

* * *

6. **이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명을 출력하는 API**

구분|값
--|--
URL|/api/institute
mehtod|GET
API 설명|이차보전 비율이 가작 작은 기관명을 노출하는 API

#### input example
    {
    }

#### output example
    {
        "region": "안양상공회의소"
    }

* * *

7. **특정 기사를 분석하여 본 사용자는 어떤 지자체에서 금융지원을 받는게 가장 좋을지 지 자체명을 추천하는 API**

구분|값
--|--
URL|/api/recommand
mehtod|GET
API 설명|특정 기사 문장을 입력하여, 문장안에 포함되어 있는 문자열들을 분석하여 추천하는 API

#### input example
    {
	    "input":"철수는 충남 대천에 살고 있는데, 은퇴하고 시설 관리 비즈니스를 하기를 원한다.시설 관리 관련 사업자들을 만나보니 관련 사업을 하려면 대체로 5 억은 필요하고, 이차보전 비 율은 2% 이내가 좋다는 의견을 듣고 정부에서 운영하는 지자체 협약 지원정보를 검색한다."
    }

#### output example
    {
        "rate": "1.0%~2.0%",
        "usage": "운전 및 시설",
        "limit": "5억원이내",
        "region": "reg0037"
    }

* * *
