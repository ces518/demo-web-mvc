package me.june.demowebmvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class SimpleControllerTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * HTTP Method ..
     *
     * GET
     * = 클라이언트가 리소스를 요청할때 사용한다.
     * ㅋㅐ싱이 가능하다.(조건 적인 GET이가능 header정보에 의해 not modified 등 의 응답으로인해 body로 보내지않아도 caching된 정보를보내주므로
     * 서버 리소스를 애낄수있다.)
     * 브라우저에 기록이 남는다.
     * 북마크가 가능하다.
     * 민감한 데이터를 보낼때 사용하지말것.
     * idempoent = 동일한 요청에는 동일한 응답을 해야한다.
     *
     *
     *  POST
     *  = 클라이언트가 서버의 리소스를 새로 생성하거나 수정할때 사용한다.
     *  서버에 보내는ㄷ 데이터를 POST요청 본문에 보낸다.
     *  캐싱이 불가능하다.
     *  브라우저 기록에 남지않는다.
     *  북마크할수없다.
     *  데이터 길이 제한이없다.
     *
     *  PUT
     *  = URI에 해당하는 데이터를 새로 생성하거나 수정할때 사용하낟.
     *  POST와 차이점은 URI의 의미가 다르다.
     *
     *  POST = 보내는 데이터를 처리할 리소스를 지칭한다.
     *  PUT = 보내는 데이터에 해당하는 리소스를 지칭한다.
     *  idempoent
     *
     *  PATCH
     *  = put과 비슷하지만 , 기존 엔티티와 수정할 엔티티의 차이점만 보낸다.
     *  리소스의 일정부분만 수정하고 싶은경우 사용한다.
     *  idempoent
     *
     *
     *  DELETE
     *  = 리소스를 삭제할때 사용하낟.
     *  idempoent
     *
     */
    @MockBean
    SimpleService simpleService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void hello() throws Exception {
        mockMvc.perform(get("/hello"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello"));
        mockMvc.perform(put("/hello"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void mockBeanTest(){
        SimpleVO value1 = new SimpleVO();
        value1.setAge("10");
        value1.setName("juneyoung");

        SimpleVO value2 = new SimpleVO();
        value2.setAge("12");
        value2.setName("mayeye");
        given(simpleService.findAll()).willReturn(Arrays.asList(value1,value2));

        List<SimpleVO> mockResult = simpleService.findAll();
        logger.info("mockResult = {}",mockResult.size());

        assertThat(mockResult.size()).isEqualTo(2);

        for (SimpleVO simple:
                                mockResult) {
            logger.info("name = {}",simple.getName());
        }
    }

    @Test
    public void uriTest() throws Exception {

        mockMvc.perform(get("/uri/o"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().string("hello o"))
                //handler에 대한 테스트도 작성이 가능하다.
                        .andExpect(handler().handlerType(SimpleController.class))
                        .andExpect(handler().methodName("helloUri"))
                        ;
    }

    @Test
    public void mediaTest() throws Exception{
        mockMvc.perform(get("/media")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("json"));
    }

    @Test
    public void methodTest() throws Exception{
        mockMvc.perform(head("/head"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(options("/head"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void uriTests() throws Exception {
        mockMvc.perform(get("/events/1;name=june"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("june"));

        mockMvc.perform(post("/events")
                        .param("name","hyeji bear"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("hyeji bear"));
    }

}