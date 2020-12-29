package com.seongbindb.javatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) // properties에서 설정
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)// 위와 아래를 같이 사용할 필요는 없다. 시나리옴 // properties에서 설정함

//@ExtendWith(FindSlowTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    // JUnit은 테스트 메소드 마다 테스트 인스턴스를 새로 만든다. 독립적으로 실행하여 예{상치 못한 부작용을 방지하기 위함
    // 테스트가 항상 순서대로 실행되는 것은 아님 (선언한대로) value는 test마다 항상 1을 가진다
    int value = 1;

    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension =
            new FindSlowTestExtension(1000L);
    @Order(2)
    @FastTest
    // @DisplayName("스터디 만들기 fast")
    // @Tag("fast") type safe하지 않다 오타가 날 가능성이 많음 오타를 줄여야함 그때 커스텀 태그
    void create_new_study() {
        Study study = new Study(value);
        assertNotNull(study);
        System.out.println("create " + value);
    }
    @Order(1)
    @SlowTest
    @DisplayName("스터디 만들기 slow")
    @Disabled
    //@Tag("slow")
    void create_new_study_again() throws InterruptedException {
        Thread.sleep(1005L);
        System.out.println("create " + value++);
    }

    // ------------- 테스트 반복하기 -------------------------
    @DisplayName("스터디 반복하기")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest(RepetitionInfo repeatInfo) {
        System.out.println("test " + repeatInfo.getCurrentRepetition() + "/" +
                repeatInfo.getTotalRepetitions());
    }

    @DisplayName("파라미터 테스트 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요"})
    @NullSource
    @EmptySource
    @NullAndEmptySource
    void parameterizedTest(String message) {
        System.out.println(message);
    }

    @DisplayName("시브에스 소스 테스트")
    @ParameterizedTest(name = "{index} {displayName} int={0}")
    @ValueSource(ints = {10,20,30})
    void parameterizedTest2(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }
    // convertor는 하나의 argument에만 사용할 수 있음
    //No implicit conversion to convert object (아래를 구현하지 않으면 오류가남 스터디 컨버터)
    public static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }


    @DisplayName("시브에스 소스 테스트")
    @ParameterizedTest(name = "{index} {displayName} int={0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void cvsSourceTest(Integer limit, String name) {
        Study study = new Study(limit, name);
        System.out.println(study);
    }

    @DisplayName("시브에스 소스 테스트")
    @ParameterizedTest(name = "{index} {displayName} int={0} name={1}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void cvsSourceTest2(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println(study);
    }
    // 위에코드를 커스텀한여 인터페이스로도 구현할 수 있다
    @DisplayName("시브에스 소스 테스트")
    @ParameterizedTest(name = "{index} {displayName} int={0} name={1}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void cvsSourceTest3(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }
    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
    }





    @BeforeAll
    static void beforeAll() {
        System.out.println("beforeAll");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("afterAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before");
    }
    @AfterEach
    void afterEach() {
        System.out.println("after");
    }
}