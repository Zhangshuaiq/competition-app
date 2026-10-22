package competition.service;

import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamRankStyleServiceTest {
    @Test
    void legacyPerRankColorsOnlyKeepTopThree() {
        ConfigsMapper mapper = mock(ConfigsMapper.class);
        TeamRankStyleService service = new TeamRankStyleService();
        ReflectionTestUtils.setField(service, "configsMapper", mapper);
        Configs config = new Configs();
        config.setConfig("{\"rowColors\":[\"#111111\",\"#222222\",\"#333333\",\"#444444\"],\"rowBackground\":\"#abcdef\"}");
        when(mapper.selectOne(any())).thenReturn(config);
        TeamRankStyleService.Style style = service.load();
        assertEquals(java.util.Arrays.asList("#111111", "#222222", "#333333"), style.getRowColors());
        assertEquals("#abcdef", style.getRowBackground());
    }

    @Test
    void defaultsAndSavedStyleRoundTrip() {
        ConfigsMapper mapper = mock(ConfigsMapper.class);
        TeamRankStyleService service = new TeamRankStyleService();
        ReflectionTestUtils.setField(service, "configsMapper", mapper);
        TeamRankStyleService.Style style = service.load();
        assertEquals(22, style.getRowFontSize());
        assertTrue(style.isShowRowBackground());
        style.setRowFontSize(36);
        style.setShowRowBackground(false);
        style.setHeaderBackground("#123456");
        style.setRowBackground("#abcdef");
        style.getRowColors().set(0, "#123123");
        service.save(style);
        ArgumentCaptor<Configs> saved = ArgumentCaptor.forClass(Configs.class);
        verify(mapper).insert(saved.capture());
        when(mapper.selectOne(any())).thenReturn(saved.getValue());
        TeamRankStyleService.Style loaded = service.load();
        assertEquals(36, loaded.getRowFontSize());
        assertFalse(loaded.isShowRowBackground());
        assertEquals("#123456", loaded.getHeaderBackground());
        assertEquals("#abcdef", loaded.getRowBackground());
        assertEquals("#123123", loaded.getRowColors().get(0));
        assertEquals(3, loaded.getRowColors().size());
        service.save(loaded);
        verify(mapper).updateById(saved.getValue());
        verify(mapper, times(1)).insert(any());
    }
}
