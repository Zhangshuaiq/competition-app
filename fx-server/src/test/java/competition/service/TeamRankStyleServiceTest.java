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
        assertTrue(style.isAutoCenter());
        assertFalse(style.isShowContainerBackground());
        assertEquals(0, style.getContentX());
        assertEquals(0, style.getContentY());
        assertEquals(java.util.Arrays.asList("#ffffff", "#ffffff", "#000000"), style.getRowTextColors());
        assertEquals("#000000", style.getRowTextColor());
    }

    @Test
    void defaultsAndSavedStyleRoundTrip() {
        ConfigsMapper mapper = mock(ConfigsMapper.class);
        TeamRankStyleService service = new TeamRankStyleService();
        ReflectionTestUtils.setField(service, "configsMapper", mapper);
        TeamRankStyleService.Style style = service.load();
        assertEquals(22, style.getRowFontSize());
        assertTrue(style.isShowRowBackground());
        style.setAutoCenter(false);
        style.setContentX(125);
        style.setContentY(-20);
        style.setShowContainerBackground(true);
        style.setContainerBackground("#456789");
        style.setRowFontSize(36);
        style.setShowRowBackground(false);
        style.setHeaderBackground("#123456");
        style.setRowBackground("#abcdef");
        style.getRowColors().set(0, "#123123");
        style.setRowTextColors(new java.util.ArrayList<>(java.util.Arrays.asList("#123456", "#456789", "#abcdef")));
        style.setRowTextColor("#654321");
        service.save(style);
        ArgumentCaptor<Configs> saved = ArgumentCaptor.forClass(Configs.class);
        verify(mapper).insert(saved.capture());
        when(mapper.selectOne(any())).thenReturn(saved.getValue());
        TeamRankStyleService.Style loaded = service.load();
        assertEquals(36, loaded.getRowFontSize());
        assertFalse(loaded.isAutoCenter());
        assertEquals(125, loaded.getContentX());
        assertEquals(-20, loaded.getContentY());
        assertTrue(loaded.isShowContainerBackground());
        assertEquals("#456789", loaded.getContainerBackground());
        assertFalse(loaded.isShowRowBackground());
        assertEquals("#123456", loaded.getHeaderBackground());
        assertEquals("#abcdef", loaded.getRowBackground());
        assertEquals("#123123", loaded.getRowColors().get(0));
        assertEquals(3, loaded.getRowColors().size());
        assertEquals(java.util.Arrays.asList("#123456", "#456789", "#abcdef"), loaded.getRowTextColors());
        assertEquals("#654321", loaded.getRowTextColor());
        service.save(loaded);
        verify(mapper).updateById(saved.getValue());
        verify(mapper, times(1)).insert(any());
    }
}
