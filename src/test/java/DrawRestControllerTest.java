
import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.service.drawStatisticsService.DrawStatisticsService;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.service.parkingSpaceService.ParkingSpaceService;
import com.cruise.parkinglotto.service.priorityApplicantService.PriorityApplicantService;
import com.cruise.parkinglotto.web.controller.DrawRestController;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.converter.DrawConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.certificateDocsDTO.CertificateDocsRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = DrawRestController.class)
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
class DrawRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicantService applicantService;

    @MockBean
    private ApplicantConverter applicantConverter;

    @MockBean
    private MemberService memberService;

    @MockBean
    private DrawStatisticsService drawStatisticsService;;

    @MockBean
    private PriorityApplicantService priorityApplicantService;

    @MockBean
    private JwtUtils jwtUtils;;

    @MockBean
    private ParkingSpaceService parkingSpaceService;

    @MockBean
    private DrawService drawService;

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"ADMIN"})
    public void testExecuteDraw() throws Exception {
        // Mocking the drawService to do nothing when executeDraw is called
        doNothing().when(drawService).executeDraw(eq(1L));

        // Perform the POST request and verify the response
        mockMvc.perform(post("/api/draws/{drawId}/execution", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify that the service method was called with the correct parameters
        verify(drawService).executeDraw(eq(1L));
    }


    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetDrawResult() throws Exception {

        Member member1 = Member.builder()
                .accountId("test-account-id")
                .carNum("1234")
                .workType(WorkType.TYPE1)
                .address("seoul").build();

        Member member2 = Member.builder()
                .accountId("test-account")
                .carNum("1234")
                .workType(WorkType.TYPE1)
                .address("seoul").build();

        Draw draw = Draw.builder()
                .id(1L)
                .build();

        // Mocking a list of applicants
        List<Applicant> applicants = Arrays.asList(
                Applicant.builder()
                        .member(member1)
                        .draw(draw).build(), // 필요한 필드를 설정
                Applicant.builder()
                        .member(member2)
                        .draw(draw).build()
        );

        // Mocking a paged result
        Page<Applicant> pagedResult = new PageImpl<>(applicants, PageRequest.of(0, 15), applicants.size());
        when(drawService.getDrawResult(any(HttpServletRequest.class), eq(1L), eq(0))).thenReturn(pagedResult);

        // Mocking the conversion to the final DTO
        DrawResponseDTO.DrawMemberResultResponseDTO responseDTO = DrawConverter.toDrawResultResponseDTO(pagedResult);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/result", 1L)
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetCurrentDrawInfo() throws Exception {
        // Mocking the response DTO
        DrawResponseDTO.GetCurrentDrawInfoDTO mockResponse = DrawResponseDTO.GetCurrentDrawInfoDTO.builder()
                .title("Sample Draw")
                .description("This is a sample draw.")
                .mapImageUrl("image")
                .totalSlots(100)
                .drawStatus(DrawStatus.OPEN)
                .build();

        // Mocking the service method
        when(drawService.getCurrentDrawInfo(any(HttpServletRequest.class), eq(1L))).thenReturn(mockResponse);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("DRAW2001"))
                .andExpect(jsonPath("$.message").value("추첨 정보를 조회했습니다."))
                .andExpect(jsonPath("$.result.title").value("Sample Draw"))
                .andExpect(jsonPath("$.result.drawStartAt").isEmpty())  // null 값 검증
                .andExpect(jsonPath("$.result.drawEndAt").isEmpty())    // null 값 검증
                .andExpect(jsonPath("$.result.usageStartAt").isEmpty()) // null 값 검증
                .andExpect(jsonPath("$.result.usageEndAt").isEmpty())   // null 값 검증
                .andExpect(jsonPath("$.result.mapImageUrl").value("image"))
                .andExpect(jsonPath("$.result.description").value("This is a sample draw."))
                .andExpect(jsonPath("$.result.totalSlots").value(100))
                .andExpect(jsonPath("$.result.drawStatus").value("OPEN"))
                .andExpect(jsonPath("$.result.parkingSpaceList").isEmpty())  // null 값 검증
                .andReturn();
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testCreateDraw() throws Exception {
        // Prepare mock data
        MockMultipartFile mockImageFile = new MockMultipartFile("mapImage", "test-image.png", MediaType.IMAGE_PNG_VALUE, "image data".getBytes());

        // Correctly named JSON part for the multipart request
        String jsonContent = "{ \"title\": \"Sample Draw\", \"description\": \"This is a sample draw\", \"type\": \"GENERAL\", " +
                "\"usageStartAt\":\"2004-08-20T08:20:00\", " +
                "\"usageEndAt\":\"2004-11-20T08:20:00\", " +
                "\"drawStartAt\":\"2004-08-19T08:20:00\", " +
                "\"drawEndAt\":\"2004-08-19T11:20:00\" }";
        MockMultipartFile mockJsonFile = new MockMultipartFile("createDrawRequestDTO", "", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        Draw mockDraw = Draw.builder()
                .id(1L)
                .title("Sample Draw")
                .description("This is a sample draw")
                .build();

        DrawResponseDTO.CreateDrawResultDTO responseDTO = DrawResponseDTO.CreateDrawResultDTO.builder()
                .drawId(mockDraw.getId())
                .title(mockDraw.getTitle())
                .build();

        // Mocking service and converter
        when(drawService.createDraw(any(MultipartFile.class), any(DrawRequestDTO.CreateDrawRequestDTO.class))).thenReturn(mockDraw);

        // Perform the POST request and verify the response
        mockMvc.perform(multipart("/api/draws")
                        .file(mockImageFile)
                        .file(mockJsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testConfirmDrawCreation() throws Exception {
        // Mocking the service method to return a predefined DTO
        DrawResponseDTO.ConfirmDrawCreationResultDTO mockResponseDTO = DrawResponseDTO.ConfirmDrawCreationResultDTO.builder()
                .drawId(1L)
                .title("draw")
                .build();

        when(drawService.confirmDrawCreation(eq(1L))).thenReturn(mockResponseDTO);

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testSimulateDraw() throws Exception {
        // Mocking the service method to return a predefined DTO

        List<DrawResponseDTO.SimulateApplicantDTO> parkingSpaceList = Arrays.asList(
                DrawResponseDTO.SimulateApplicantDTO.builder().build(),
                DrawResponseDTO.SimulateApplicantDTO.builder().build()
        );

        DrawResponseDTO.SimulateDrawResponseDTO mockResponseDTO = DrawResponseDTO.SimulateDrawResponseDTO.builder()
                .drawId(1L)
                .seed("12345")
                .winnerList(parkingSpaceList)
                .totalApplicantCount(200)
                .build();

        when(drawService.simulateDraw(eq(1L), eq("12345"), eq(1))).thenReturn(mockResponseDTO);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/simulation", 1L)
                        .param("seedNum", "12345")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetDrawInfo() throws Exception {
        // Mocking the service method to return a predefined DTO
        DrawResponseDTO.GetDrawOverviewResultDTO mockResponseDTO = DrawResponseDTO.GetDrawOverviewResultDTO.builder()
                .drawId(1L)
                .title("Draw Sample")
                .status(DrawStatus.OPEN)
                .isApplied(true)
                .applicantsCount(300)
                .totalSlots(200)
                .build();

        when(drawService.getDrawOverview(any(HttpServletRequest.class))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"DRAW2007\",\"message\":\"추첨 개요 정보를 조회하였습니다.\",\"result\":{\"drawId\":1,\"applicantsCount\":300,\"totalSlots\":200,\"applied\":true,\"status\":\"OPEN\"}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testAddParkingSpace() throws Exception {
        // Mocking the floor plan image file
        MockMultipartFile mockImageFile = new MockMultipartFile("floorPlanImage", "floorplan.png", MediaType.IMAGE_PNG_VALUE, "image data".getBytes());

        // Mocking the addParkingSpaceDTO as a JSON part
        String jsonContent = "{ \"zoneName\": \"Zone A\", \"capacity\": 50 }";
        MockMultipartFile mockJsonFile = new MockMultipartFile("addParkingSpaceDTO", "", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        // Mocking the service method to return a predefined ParkingSpace object
        ParkingSpace mockParkingSpace = ParkingSpace.builder()
                .id(1L)
                .address("seoul")
                .slots(200)
                .floorPlanImageUrl("imageUrl")
                .build();

//        ParkingSpaceResponseDTO.AddParkingSpaceResultDTO mockResponseDTO = ParkingSpaceResponseDTO.AddParkingSpaceResultDTO.builder()
//                .parkingSpaceId(mockParkingSpace.getId())
//                .zoneName(mockParkingSpace.getZoneName())
//                .capacity(mockParkingSpace.getCapacity())
//                .build();

        when(parkingSpaceService.addParkingSpace(eq(1L), any(MockMultipartFile.class), any(ParkingSpaceRequestDTO.AddParkingSpaceDTO.class)))
                .thenReturn(mockParkingSpace);

        // Perform the POST request and verify the response
        mockMvc.perform(multipart("/api/draws/{drawId}/parking-spaces", 1L)
                        .file(mockImageFile)
                        .file(mockJsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json("{\"isSuccess\":true,\"time\":null,\"code\":\"PARKING_SPACE_ADDED\",\"message\":null,\"result\":{\"parkingSpaceId\":1,\"zoneName\":\"Zone A\",\"capacity\":50}}"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetApplicantList() throws Exception {

        Member member1 = Member.builder()
                .accountId("test-account-id")
                .carNum("1234")
                .workType(WorkType.TYPE1)
                .address("seoul").build();

        Member member2 = Member.builder()
                .accountId("test-account")
                .carNum("1235")
                .workType(WorkType.TYPE1)
                .address("seoul").build();

        // Mocking a list of applicants
        Applicant applicant1 = Applicant.builder().id(1L).member(member1).build();
        Applicant applicant2 = Applicant.builder().id(2L).member(member2).build();
        List<Applicant> applicants = Arrays.asList(applicant1, applicant2);
        List<ApplicantResponseDTO.GetApplicantResultDTO> applicantResultList = Arrays.asList(
                ApplicantResponseDTO.GetApplicantResultDTO.builder().build(),
                ApplicantResponseDTO.GetApplicantResultDTO.builder().build()
        );
        // Mocking a paged result
        Page<Applicant> pagedResult = new PageImpl<>(applicants, PageRequest.of(0, 10), applicants.size());

        // Mocking the service method to return the paged result
        when(applicantService.getApplicantList(eq(0), eq(1L))).thenReturn(pagedResult);

        // Mocking the converter to convert the paged result to a DTO

        // Expected JSON response
//        String expectedJson = "{\"isSuccess\":true,\"time\":null,\"code\":\"APPLICANT_LIST_FOUND\",\"message\":null,\"result\":{\"applicants\":[{\"id\":1,\"name\":\"Applicant 1\"},{\"id\":2,\"name\":\"Applicant 2\"}],\"totalPages\":1,\"totalElements\":2}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/applicants", 1L)
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testDrawApply() throws Exception {
        // Mocking JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the certificate documents
        MockMultipartFile mockCertificateFile = new MockMultipartFile("certificateDocs", "certificate.pdf", MediaType.APPLICATION_PDF_VALUE, "test pdf content".getBytes());

        // Mocking the DTO as a JSON part
        String jsonContent = "{ \"carNum\": \"34가1239\", \"address\": \"seoul\",\"distance\":\"123\" }";  // Replace with actual JSON structure of GeneralApplyDrawRequestDTO
        MockMultipartFile mockJsonFile = new MockMultipartFile("applyDrawRequestDTO", "", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        // Mocking the service method to do nothing
        doNothing().when(applicantService).drawApply(any(List.class), any(ApplicantRequestDTO.GeneralApplyDrawRequestDTO.class), eq(mockAccountId), eq(1L));

        // Perform the POST request and verify the response
        mockMvc.perform(multipart("/api/draws/{drawId}/general/apply", 1L)
                        .file(mockCertificateFile)
                        .file(mockJsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"APPLICANT2003\",\"message\":\"신청에 성공했습니다.\"}"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testApprovePriority() throws Exception {
        // Mocking the service method to return a predefined DTO
        PriorityApplicantResponseDTO.ApprovePriorityResultDTO mockResponseDTO = PriorityApplicantResponseDTO.ApprovePriorityResultDTO.builder()
                .priorityApplicantId(1L)
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        when(priorityApplicantService.approvePriority(eq(1L), eq(2L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"PRIORITY2002\",\"message\":\"해당 사용자에게 우대 신청 승인을 완료했습니다.\",\"result\":{\"priorityApplicantId\":1,\"approvalStatus\":\"APPROVED\"}}";

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}/priority-applicants/{priorityApplicantId}/approval", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }


    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testSearchApplicant() throws Exception {
        // Mocking a list of applicants
        Member member1 = Member.builder()
                .accountId("test-account-id")
                .carNum("1234")
                .workType(WorkType.TYPE1)
                .address("seoul").build();

        Member member2 = Member.builder()
                .accountId("test-account")
                .carNum("1235")
                .workType(WorkType.TYPE1)
                .address("seoul").build();

        // Mocking a list of applicants
        Applicant applicant1 = Applicant.builder().id(1L).member(member1).build();
        Applicant applicant2 = Applicant.builder().id(2L).member(member2).build();
        List<Applicant> applicants = Arrays.asList(applicant1, applicant2);
        List<ApplicantResponseDTO.GetApplicantResultDTO> applicantList = Arrays.asList(
                ApplicantResponseDTO.GetApplicantResultDTO.builder().build(),
                ApplicantResponseDTO.GetApplicantResultDTO.builder().build()
        );

        // Mocking a paged result
        Page<Applicant> pagedResult = new PageImpl<>(applicants, PageRequest.of(0, 10), applicants.size());

        // Mocking the service method to return the paged result
        when(applicantService.searchApplicant(eq(0), eq("John"), eq(1L))).thenReturn(pagedResult);

        // Mocking the converter to convert the paged result to a DTO



        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"APPLICANT2006\",\"message\":\"신청자 검색을 완료했습니다.\"" +
                ",\"result\":{\"applicantList\":[{\"applicantId\":1,\"memberId\":null,\"employeeNo\":null,\"nameKo\":null," +
                "\"deptPathName\":null},{\"applicantId\":2,\"memberId\":null,\"employeeNo\":null,\"nameKo\":null,\"deptPathName\":null}]," +
                "\"listSize\":10,\"totalPage\":1,\"totalElements\":2,\"isFirst\":true,\"isLast\":true}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/applicants/search", 1L)
                        .param("keyword", "John")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testSearchWinner() throws Exception {

        Member member1 = Member.builder()
                .id(1L)
                .accountId("test-account-id")
                .carNum("1234")
                .workType(WorkType.TYPE1)
                .address("seoul")
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .accountId("test-account")
                .carNum("1234")
                .workType(WorkType.TYPE1)
                .address("seoul")
                .build();
        Draw draw = Draw.builder()
                .id(1L)
                .build();
        // Mocking a paged result from the applicantService
        List<Applicant> applicants = Arrays.asList(
                Applicant.builder()
                        .member(member1)
                        .draw(draw).build(),
                Applicant.builder()
                        .draw(draw)
                        .member(member2).build()
        );


        Page<Applicant> pagedResult = new PageImpl<>(applicants, PageRequest.of(0, 10), applicants.size());
        when(applicantService.searchWinner(eq(0), eq("test"), eq(1L))).thenReturn(pagedResult);

        // Mocking the conversion to the final DTO
        ApplicantResponseDTO.GetApplicantListResultDTO resultDTO =  ApplicantConverter.toGetApplicantListResultDTO(pagedResult);

//        when(ApplicantConverter.toGetApplicantListResultDTO(pagedResult)).thenReturn(resultDTO);
        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/winners/search",1L)
                        .param("keyword", "test")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // You can add jsonPath assertions here to verify the response content
                .andReturn();
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testDownloadResultExcel() throws Exception {
        // Mocking the service method to return a predefined DTO
        DrawResponseDTO.DrawResultExcelDTO mockResponseDTO = DrawResponseDTO.DrawResultExcelDTO.builder()
                .url("/path/to/lottery_result.xlsx")
                .build();

        when(drawService.getDrawResultExcel(eq(1L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"DRAW2009\",\"message\":\"추첨결과의 URL이 정상적으로 전송되었습니다.\",\"result\":{\"url\":\"/path/to/lottery_result.xlsx\"}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/result-excel", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetDrawSeedInfoResult() throws Exception {
        // Mocking the service method to return a predefined DTO
        List<DrawResponseDTO.SeedDetailDTO> seedDetails = Arrays.asList(
                DrawResponseDTO.SeedDetailDTO.builder()
                        .accountId("test-account-id").build(),
                DrawResponseDTO.SeedDetailDTO.builder()
                        .accountId("test-account").build()
        );
        DrawResponseDTO.GetDrawInfoDetailDTO mockResponseDTO = DrawResponseDTO.GetDrawInfoDetailDTO.builder()
                .drawId(1L)
                .seed("A")
                .applicantsCount(300)
                .title("추첨")
                .totalSlots(200)
                .seedDetailList(seedDetails)
                .build();

        when(drawService.getDrawInfoDetail(any(HttpServletRequest.class), eq(1L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"DRAW2001\",\"message\":\"추첨 정보를 조회했습니다.\",\"result\":{\"drawId\":1,\"title\":\"추첨\",\"applicantsCount\":300,\"totalSlots\":200,\"seed\":\"A\",\"seedDetailList\":[{\"accountId\":\"test-account-id\",\"userSeed\":null},{\"accountId\":\"test-account\",\"userSeed\":null}]}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/result/seed", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetPriorityApplicantDetails() throws Exception {
        // Mocking the service method to return a predefined DTO

        List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFiles = Arrays.asList(
                CertificateDocsRequestDTO.CertificateFileDTO.builder().build(),
                CertificateDocsRequestDTO.CertificateFileDTO.builder().build(),
                CertificateDocsRequestDTO.CertificateFileDTO.builder().build()
        );

        PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO mockResponseDTO = PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO.builder()
                .priorityApplicantId(1L)
                .nameKo("John Doe")
                .employeeNo("emp1123")
                .accountId("test-account-id")
                .deptPathName("Detailed applicant information")
                .certificateFileList(certificateFiles)
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        when(priorityApplicantService.getPriorityApplicantDetails(eq(1L), eq(2L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson =
                "{\"isSuccess\":true,\"code\":\"PRIORITY2003\"," +
                        "\"message\":\"해당 우대 신청자의 신청 정보를 조회하였습니다.\"," +
                        "\"result\":{\"priorityApplicantId\":1,\"nameKo\":\"John Doe\"," +
                        "\"employeeNo\":\"emp1123\",\"accountId\":\"test-account-id\"," +
                        "\"deptPathName\":\"Detailed applicant information\"," +
                        "\"certificateFileList\":[{\"fileUrl\":null,\"fileName\":null}," +
                        "{\"fileUrl\":null,\"fileName\":null},{\"fileUrl\":null,\"fileName\":null}]," +
                        "\"approvalStatus\":\"APPROVED\"}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/priority-applicants/{priorityApplicantId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testRejectPriority() throws Exception {
        // Mocking the service method to return a predefined DTO
        PriorityApplicantResponseDTO.RejectPriorityResultDTO mockResponseDTO = PriorityApplicantResponseDTO.RejectPriorityResultDTO.builder()
                .priorityApplicantId(2L)
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        when(priorityApplicantService.rejectPriority(eq(1L), eq(2L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"PRIORITY2004\",\"message\":\"해당 사용자의 우대신청을 거절하였습니다.\",\"result\":{\"priorityApplicantId\":2,\"approvalStatus\":\"APPROVED\"}}";

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}/priority-applicants/{priorityApplicantId}/rejection", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetDrawList() throws Exception {
        // Mocking the service method to return a predefined DTO
        DrawResponseDTO.GetDrawListResultDTO mockResponseDTO = DrawResponseDTO.GetDrawListResultDTO.builder()
                .drawList(Arrays.asList(
                        DrawResponseDTO.DrawPreviewDTO.builder().drawId(1L).drawTitle("Draw 1").drawStatus(DrawStatus.OPEN).build(),
                        DrawResponseDTO.DrawPreviewDTO.builder().drawId(2L).drawTitle("Draw 2").drawStatus(DrawStatus.COMPLETED).build()
                ))
                .yearList(Arrays.asList("2024","2023","2022"))
                .build();

        when(drawService.getDrawList(eq("2023"), eq(DrawType.GENERAL))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"DRAW2011\",\"message\":\"추첨 목록을 조회하였습니다.\",\"result\":{\"yearList\":[\"2024\",\"2023\",\"2022\"],\"drawList\":[{\"drawId\":1,\"drawType\":null,\"drawTitle\":\"Draw 1\",\"drawStatus\":\"OPEN\"},{\"drawId\":2,\"drawType\":null,\"drawTitle\":\"Draw 2\",\"drawStatus\":\"COMPLETED\"}]}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws")
                        .param("year", "2023")
                        .param("drawType", "GENERAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetYearList() throws Exception {
        // Mocking the service method to return a predefined DTO
        DrawResponseDTO.GetYearsFromDrawListDTO mockResponseDTO = DrawResponseDTO.GetYearsFromDrawListDTO.builder()
                .yearList(Arrays.asList("2023", "2022", "2021"))
                .build();

        when(drawService.getYearsFromDrawList()).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"DRAW2012\",\"message\":\"추첨이 존재하는 연도 목록을 조회하였습니다.\",\"result\":{\"yearList\":[\"2023\",\"2022\",\"2021\"]}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/years")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"ADMIN"})
    public void testCancelApplicant() throws Exception {
        // Mocking the service method to do nothing
        doNothing().when(drawService).adminCancelWinner(any(), eq(1L), eq(2L));

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}/applicants/{applicantId}/admin-cancel", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"DRAW2013\",\"message\":\"강제 취소에 성공하였습니다.\"}"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testDrawPriorityApply() throws Exception {
        // Mocking JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the certificate documents
        MockMultipartFile generalCertificateFile = new MockMultipartFile("GeneralCertificateDocs", "general_certificate.pdf", MediaType.APPLICATION_PDF_VALUE, "general pdf content".getBytes());
        MockMultipartFile priorityCertificateFile = new MockMultipartFile("priorityCertificateDocs", "priority_certificate.pdf", MediaType.APPLICATION_PDF_VALUE, "priority pdf content".getBytes());

        // Mocking the DTO as a JSON part
        String jsonContent = "{ \"carNum\": \"324가1239\", \"address\": \"seoul\", \"distance\": \"123\" }";  // Replace with actual JSON structure of PriorityApplyDrawRequestDTO
        MockMultipartFile mockJsonFile = new MockMultipartFile("applyDrawRequestDTO", "", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        // Mocking the service method to do nothing
        doNothing().when(priorityApplicantService).drawPriorityApply(any(List.class), any(List.class), any(PriorityApplicantRequestDTO.PriorityApplyDrawRequestDTO.class), eq(mockAccountId), eq(1L));

        // Perform the POST request and verify the response
        mockMvc.perform(multipart("/api/draws/{drawId}/priority/apply", 1L)
                        .file(generalCertificateFile)
                        .file(priorityCertificateFile)
                        .file(mockJsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"APPLICANT2003\",\"message\":\"신청에 성공했습니다.\"}"))
                .andDo(print());
    }


    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testSelfCancel() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the service method to do nothing
        doNothing().when(drawService).selfCancelWinner(any(HttpServletRequest.class), eq(1L));

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}/self-cancel", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"DRAW2014\",\"message\":\"취소에 성공하였습니다.\"}"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetDrawStatistics() throws Exception {
        // Mocking the service method to return a predefined DTO

        List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRateList = Arrays.asList(
                ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO.builder().build()      ,
                ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO.builder().build()      ,
                ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO.builder().build()      ,
                ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO.builder().build()
        );

        List<DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO> winningRatePerWeightSectionList = Arrays.asList(
                DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO.builder().build()      ,
                DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO.builder().build()      ,
                DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO.builder().build()      ,
                DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO.builder().build()
        );

        DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO mockResponseDTO = DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO.builder()
                .applicantsCount(300)
                .winnersWeightAvg(60.00)
                .drawTitle("추첨")
                .totalSlots(200)
                .trafficCommuteTimeAvg(80.00)
                .carCommuteTimeAvg(90.00)
                .distanceAvg(40.00)
                .recentLossCountAvg(2.33)
                .dominantWorkType(WorkType.TYPE1)
                .parkingSpaceCompetitionRateList(parkingSpaceCompetitionRateList)
                .winningRatePerWeightSectionList(winningRatePerWeightSectionList)
                .build();

        when(drawStatisticsService.getDrawStatistics(eq(1L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true," +
                "\"code\":\"DRAW2008\"," +
                "\"message\":\"추첨의 통계를 조회하였습니다.\"," +
                "\"result\":{\"applicantsCount\":300,\"winnersWeightAvg\":60.0,\"drawTitle\":\"추첨\",\"totalSlots\":200,\"trafficCommuteTimeAvg\":80.0,\"carCommuteTimeAvg\":90.0,\"distanceAvg\":40.0,\"recentLossCountAvg\":2.33,\"dominantWorkType\":\"TYPE1\"," +
                "\"parkingSpaceCompetitionRateList\":[{\"parkingSpaceId\":null,\"name\":null,\"slots\":null,\"applicantsCount\":null},{\"parkingSpaceId\":null,\"name\":null,\"slots\":null,\"applicantsCount\":null}," +
                "{\"parkingSpaceId\":null,\"name\":null,\"slots\":null,\"applicantsCount\":null}," +
                "{\"parkingSpaceId\":null,\"name\":null,\"slots\":null,\"applicantsCount\":null}]," +
                "\"winningRatePerWeightSectionList\":[{\"weightSection\":null,\"applicantsCount\":null,\"winnerCount\":null}," +
                "{\"weightSection\":null,\"applicantsCount\":null,\"winnerCount\":null}," +
                "{\"weightSection\":null,\"applicantsCount\":null,\"winnerCount\":null}," +
                "{\"weightSection\":null,\"applicantsCount\":null,\"winnerCount\":null}]}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/statistics", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"ADMIN"})
    public void testAssignPriority() throws Exception {
        // Mocking the service method to return a predefined DTO
        PriorityApplicantResponseDTO.AssignPriorityResultListDTO mockResponseDTO = PriorityApplicantResponseDTO.AssignPriorityResultListDTO.builder()
                .assignPriorityResultList(Arrays.asList(
                        PriorityApplicantResponseDTO.AssignPriorityResultDTO.builder()
                                .priorityApplicantId(1L)
                                .parkingSpaceId(1L)
                                .approvalStatus(ApprovalStatus.APPROVED)
                                .build(),
                        PriorityApplicantResponseDTO.AssignPriorityResultDTO.builder()
                                .priorityApplicantId(2L)
                                .parkingSpaceId(2L)
                                .approvalStatus(ApprovalStatus.APPROVED)
                                .build()
                ))
                .build();

        when(priorityApplicantService.assignPriority(eq(1L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"PRIORITY2005\",\"message\":\"승인 목록의 신청자들에게 주차구역 배정을 완료했습니다.\",\"result\":{\"assignPriorityResultList\":[{\"priorityApplicantId\":1,\"parkingSpaceId\":1,\"approvalStatus\":\"APPROVED\"},{\"priorityApplicantId\":2,\"parkingSpaceId\":2,\"approvalStatus\":\"APPROVED\"}]}}";

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}/priority-applicants/approved/assignment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testCancelApply() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the service method to do nothing
        doNothing().when(applicantService).cancelApply(eq(mockAccountId), eq(1L));

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/draws/{drawId}/general/apply", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"APPLICANT2008\",\"message\":\"일반 취소 신청에 성공했습니다.\"}"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testCancelPriorityApply() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the service method to do nothing
        doNothing().when(priorityApplicantService).cancelPriorityApply(eq(mockAccountId), eq(1L));

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/draws/{drawId}/priority/apply", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"PRIORITY2006\",\"message\":\"우대 취소 신청에 성공했습니다.\"}"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"ADMIN"})
    public void testCancelPriorityAssign() throws Exception {
        // Mocking the service method to return a predefined DTO
        PriorityApplicantResponseDTO.CancelPriorityAssignResultDTO mockResponseDTO = PriorityApplicantResponseDTO.CancelPriorityAssignResultDTO.builder()
                .priorityApplicantId(1L)
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        when(priorityApplicantService.cancelPriorityAssign(eq(1L), eq(2L))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"PRIORITY2007\",\"message\":\"해당 우대 신청자의 승인 및 구역 배정을 취소하였습니다.\",\"result\":{\"priorityApplicantId\":1,\"parkingSpaceId\":null,\"approvalStatus\":\"APPROVED\"}}";

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/api/draws/{drawId}/priority-applicants/assigned/{priorityApplicantId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetApplyInformation() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the service method to return a predefined DTO
        ApplicantResponseDTO.getMyApplyInformationDTO mockResponseDTO = ApplicantResponseDTO.getMyApplyInformationDTO.builder()
                .userSeed("A")
                .carNum("34가1239")
                .address("seoul")
                .firstChoice(1L)
                .secondChoice(2L)
                .workType(WorkType.TYPE1)
                .recentLossCount(3)
                .build();

        when(applicantService.getMyApplyInformation(eq(1L), eq(mockAccountId))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson = "{\"isSuccess\":true,\"code\":\"APPLICANT2009\"," +
                "\"message\":\"해당 사용자의 회차 추첨 정보 조회에 성공했습니다\"," +
                "\"result\":{\"carNum\":\"34가1239\",\"address\":\"seoul\",\"workType\":\"TYPE1\",\"firstChoice\":1," +
                "\"secondChoice\":2,\"userSeed\":\"A\",\"recentLossCount\":3,\"certificateFiles\":null}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/general/apply", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetPriorityApplyInformation() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFileList = Arrays.asList(
                CertificateDocsRequestDTO.CertificateFileDTO.builder().build(),
                CertificateDocsRequestDTO.CertificateFileDTO.builder().build(),
                CertificateDocsRequestDTO.CertificateFileDTO.builder().build()
        );

        List<CertificateDocsRequestDTO.CertificateFileDTO> priorityCertificateFileList = Arrays.asList(
                CertificateDocsRequestDTO.CertificateFileDTO.builder()
                        .fileName("Priority")
                        .build(),
                CertificateDocsRequestDTO.CertificateFileDTO.builder()
                        .fileName("Priority")
                        .build(),
                CertificateDocsRequestDTO.CertificateFileDTO.builder()
                        .fileName("Priority")
                        .build()
        );

        // Mocking the service method to return a predefined DTO
        PriorityApplicantResponseDTO.getMyPriorityApplyInformationDTO mockResponseDTO = PriorityApplicantResponseDTO.getMyPriorityApplyInformationDTO.builder()
                .carNum("34가1239")
                .generalCertificateFiles(certificateFileList)
                .priorityCertificateFiles(priorityCertificateFileList)
                .build();

        when(priorityApplicantService.getMyPriorityApplyInformation(eq(1L), eq(mockAccountId))).thenReturn(mockResponseDTO);

        // Expected JSON response
        String expectedJson =
                "{\"isSuccess\":true,\"code\":\"PRIORITY2003\"," +
                        "\"message\":\"해당 우대 신청자의 신청 정보를 조회하였습니다.\"," +
                        "\"result\":{\"carNum\":\"34가1239\"," +
                        "\"generalCertificateFiles\":[{\"fileUrl\":null,\"fileName\":null},{\"fileUrl\":null,\"fileName\":null}," +
                        "{\"fileUrl\":null,\"fileName\":null}]," +
                        "\"priorityCertificateFiles\":[{\"fileUrl\":null,\"fileName\":\"Priority\"},{\"fileUrl\":null,\"fileName\":\"Priority\"}," +
                        "{\"fileUrl\":null,\"fileName\":\"Priority\"}]}}";

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/priority/apply", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"ADMIN"})
    public void testCancelDraw() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the service method to do nothing
        doNothing().when(drawService).deleteDraw(eq(1L), eq(mockAccountId));

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/draws/{drawId}/delete", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isSuccess\":true,\"code\":\"DRAW2015\",\"message\":\"추첨 삭제를 성공했습니다\"}"))
                .andDo(print());
    }


    @Test
    @DisplayName("추첨 실행 테스트")
    @WithMockUser(username = "test-account-id", roles = {"USER"})
    public void testGetParkingSpaceInfo() throws Exception {
        // Mocking the JWT utility to return a fixed account ID
        String mockAccountId = "test-account-id";
        when(jwtUtils.getAccountIdFromRequest(any(HttpServletRequest.class))).thenReturn(mockAccountId);

        // Mocking the MemberService to return a Member object
        Member mockMember = Member.builder()
                .id(1L)
                .accountId(mockAccountId)
                .build();
        when(memberService.getMemberByAccountId(mockAccountId)).thenReturn(mockMember);

        // Mocking the ParkingSpaceService to return a ParkingSpaceInfoResponseDTO
        ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO mockResponse = ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO.builder()
                .address("123 Parking St.")
                .name("Zone A")
                .mapImageUrl("image-url")
                .address("서울")
                .title("2024 1분기 추첨")
                .build();
        when(parkingSpaceService.getParkingSpaceInfo(eq(1L), eq(1L))).thenReturn(mockResponse);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/draws/{drawId}/my-space", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Add jsonPath assertions to verify the response content
                .andReturn();

        // Verify that the service method was called with the correct parameters
        verify(parkingSpaceService).getParkingSpaceInfo(eq(mockMember.getId()), eq(1L));
    }
}