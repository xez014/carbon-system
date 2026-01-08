package com.example.assets.service.impl;

import com.example.common.entity.CarbonQuota;
import com.example.common.entity.CarbonQuotaDetail;
import com.example.common.exception.CarbonQuotaNotFoundException;
import com.example.assets.mapper.CarbonQuotaDetailMapper;
import com.example.assets.mapper.CarbonQuotaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarbonQuotaServiceImplTest {

    @Mock
    private CarbonQuotaMapper carbonQuotaMapper;

    @Mock
    private CarbonQuotaDetailMapper carbonQuotaDetailMapper;

    @InjectMocks
    private CarbonQuotaServiceImpl carbonQuotaService;

    private CarbonQuota carbonQuota;

    @BeforeEach
    void setUp() {
        carbonQuota = new CarbonQuota();
        carbonQuota.setId(1L);
        carbonQuota.setUserId(100L);
        carbonQuota.setYear(2024);
        carbonQuota.setTotalQuota(BigDecimal.valueOf(1000));
        carbonQuota.setVerifiedEmission(BigDecimal.valueOf(800));
        carbonQuota.setStatus(1);
        carbonQuota.setCreatedTime(LocalDateTime.now());
        carbonQuota.setUpdatedTime(LocalDateTime.now());
    }

    @Test
    void testGetQuotaByUserIdAndYear() {
        when(carbonQuotaMapper.selectOne(any())).thenReturn(carbonQuota);

        CarbonQuota result = carbonQuotaService.getQuotaByUserIdAndYear(100L, 2024);

        assertNotNull(result);
        assertEquals(1000, result.getTotalQuota().intValue());
        verify(carbonQuotaMapper, times(1)).selectOne(any());
    }

    @Test
    void testListQuotasByUserId() {
        when(carbonQuotaMapper.selectList(any())).thenReturn(Collections.singletonList(carbonQuota));

        List<CarbonQuota> result = carbonQuotaService.listQuotasByUserId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carbonQuotaMapper, times(1)).selectList(any());
    }

    @Test
    void testListQuotaDetails() {
        CarbonQuotaDetail detail = new CarbonQuotaDetail();
        detail.setId(1L);
        detail.setQuotaId(1L);
        detail.setType("adjust");
        detail.setAmount(BigDecimal.valueOf(100));
        detail.setBalance(BigDecimal.valueOf(1100));
        detail.setRemark("测试调整");
        detail.setChangeDate(LocalDateTime.now());

        when(carbonQuotaDetailMapper.selectList(any())).thenReturn(Collections.singletonList(detail));

        List<CarbonQuotaDetail> result = carbonQuotaService.listQuotaDetails(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carbonQuotaDetailMapper, times(1)).selectList(any());
    }

    @Test
    void testAddQuota() {
        when(carbonQuotaMapper.insert(any())).thenAnswer(invocation -> {
            CarbonQuota quota = invocation.getArgument(0);
            quota.setId(1L);
            return 1;
        });

        CarbonQuota newQuota = new CarbonQuota();
        newQuota.setUserId(100L);
        newQuota.setYear(2024);
        newQuota.setTotalQuota(BigDecimal.valueOf(1000));

        CarbonQuota result = carbonQuotaService.addQuota(newQuota);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedTime());
        assertNotNull(result.getUpdatedTime());
        verify(carbonQuotaMapper, times(1)).insert(any());
    }

    @Test
    void testUpdateQuota() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(carbonQuota);
        when(carbonQuotaMapper.updateById(any())).thenReturn(1);

        carbonQuota.setTotalQuota(BigDecimal.valueOf(1200));

        CarbonQuota result = carbonQuotaService.updateQuota(carbonQuota);

        assertNotNull(result);
        assertEquals(1200, result.getTotalQuota().intValue());
        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, times(1)).updateById(any());
    }

    @Test
    void testUpdateQuota_NotFound() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(null);

        assertThrows(CarbonQuotaNotFoundException.class, () -> {
            carbonQuotaService.updateQuota(carbonQuota);
        });

        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, never()).updateById(any());
    }

    @Test
    void testAdjustQuota() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(carbonQuota);
        when(carbonQuotaMapper.updateById(any())).thenReturn(1);
        when(carbonQuotaDetailMapper.insert(any())).thenAnswer(invocation -> {
            CarbonQuotaDetail detail = invocation.getArgument(0);
            detail.setId(1L);
            return 1;
        });

        CarbonQuota result = carbonQuotaService.adjustQuota(1L, BigDecimal.valueOf(200), "increase", "测试增加配额");

        assertNotNull(result);
        assertEquals(1200, result.getTotalQuota().intValue());
        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, times(1)).updateById(any());
        verify(carbonQuotaDetailMapper, times(1)).insert(any());
    }

    @Test
    void testAdjustQuota_NotFound() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(null);

        assertThrows(CarbonQuotaNotFoundException.class, () -> {
            carbonQuotaService.adjustQuota(1L, BigDecimal.valueOf(200), "increase", "测试增加配额");
        });

        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, never()).updateById(any());
        verify(carbonQuotaDetailMapper, never()).insert(any());
    }

    @Test
    void testFulfillQuota() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(carbonQuota);
        when(carbonQuotaMapper.updateById(any())).thenReturn(1);

        CarbonQuota result = carbonQuotaService.fulfillQuota(1L, BigDecimal.valueOf(900));

        assertNotNull(result);
        assertEquals(900, result.getVerifiedEmission().intValue());
        assertEquals(1, result.getStatus());
        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, times(1)).updateById(any());
    }

    @Test
    void testFulfillQuota_NotFulfilled() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(carbonQuota);
        when(carbonQuotaMapper.updateById(any())).thenReturn(1);

        CarbonQuota result = carbonQuotaService.fulfillQuota(1L, BigDecimal.valueOf(1100));

        assertNotNull(result);
        assertEquals(1100, result.getVerifiedEmission().intValue());
        assertEquals(0, result.getStatus());
        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, times(1)).updateById(any());
    }

    @Test
    void testFulfillQuota_NotFound() {
        when(carbonQuotaMapper.selectById(1L)).thenReturn(null);

        assertThrows(CarbonQuotaNotFoundException.class, () -> {
            carbonQuotaService.fulfillQuota(1L, BigDecimal.valueOf(900));
        });

        verify(carbonQuotaMapper, times(1)).selectById(1L);
        verify(carbonQuotaMapper, never()).updateById(any());
    }
}
