package competition.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.match.ReloadPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.ConfigSpliter;
import competition.util.PreviewStack;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PageConfig {
    
    
    @Resource
    private ControlPane controlPane;
    
    @FXML
    RadioButton thq, thh, tgh;
    
    private ToggleGroup txGroup;
    
    @FXML
    RadioButton fkh, ykh, kg, wu;

    private ToggleGroup txbzGroup;
    
    @FXML
    RadioButton hxfb, zydl, jinyong;
    
    private ToggleGroup jfpGroup;

    @FXML
    private TextField hj;

    @FXML
    private CheckBox xscjan;

    @FXML
    private ColorPicker qdembj;

    @FXML
    private TextField pmg;

    @FXML
    private ColorPicker xtzs;

    @FXML
    private TextField xsxs;

    @FXML
    private TextField pmk;

    @FXML
    private TextField qdtdmc;

    @FXML
    private ColorPicker smbj;

    @FXML
    private TextField pmx;

    @FXML
    private TextField pmy;

    @FXML
    private ColorPicker bhbj;

    @FXML
    private TextField qdbhbl;

    @FXML
    private ColorPicker xsembj;

    @FXML
    private TextField jjg;

    @FXML
    private TextField djszh;

    @FXML
    private ColorPicker hjmc;

    @FXML
    private TextField jjk;

    @FXML
    private TextField zh;

    @FXML
    private TextField xsfs;

    @FXML
    private ColorPicker pmbj;

    @FXML
    private ColorPicker dwbj;

    @FXML
    private TextField dwg;

    @FXML
    private TextField jjy;

    @FXML
    private TextField dwk;

    @FXML
    private CheckBox xshjlb;

    @FXML
    private TextField jjx;

    @FXML
    private TextField jjfs;

    @FXML
    private TextField bhbl;

    @FXML
    private ColorPicker qdsmbj;

    @FXML
    private TextField djsy;

    @FXML
    private TextField dwx;

    @FXML
    private TextField djsx;

    @FXML
    private TextField dwy;

    @FXML
    private TextField daqz;

    @FXML
    private ColorPicker jjzs;

    @FXML
    private ColorPicker xszs;

    @FXML
    private ColorPicker bt;

    @FXML
    private ColorPicker gzzs;

    @FXML
    private ColorPicker qdymbj;

    @FXML
    private ColorPicker ymbj;

    @FXML
    private ColorPicker jjymbj;

    @FXML
    private ColorPicker mcbj;

    @FXML
    private ColorPicker xxbj;

    @FXML
    private ColorPicker tg;

    @FXML
    private TextField tdmcbl;

    @FXML
    private ColorPicker da;

    @FXML
    private ColorPicker zttcw;

    @FXML
    private ColorPicker xsymbj;

    @FXML
    private ColorPicker dm;

    @FXML
    private ColorPicker jjsmbj;

    @FXML
    private ColorPicker zttdd;

    @FXML
    private TextField qdsj;

    @FXML
    private TextField mrzh;

    @FXML
    private TextField hjg;

    @FXML
    private TextField jjsj;

    @FXML
    private TextField hjk;

    @FXML
    private ColorPicker gdxt;

    @FXML
    private TextField jjtdmc;

    @FXML
    private ColorPicker djs;

    @FXML
    private ColorPicker qdbj;

    @FXML
    private CheckBox xsbt;

    @FXML
    private TextField qdg;

    @FXML
    private TextField jjbhbl;

    @FXML
    private TextField hjy;

    @FXML
    private TextField hjx;

    @FXML
    private ColorPicker qdzs;

    @FXML
    private TextField qdk;

    @FXML
    private ColorPicker jjbj;

    @FXML
    private ColorPicker xsbj;

    @FXML
    private ColorPicker pmzs;

    @FXML
    private TextField xsbhbl;

    @FXML
    private TextField qdx;

    @FXML
    private TextField qdy;

    @FXML
    private ColorPicker fs;

    @FXML
    private ColorPicker xssmbj;

    @FXML
    private TextField xsg;

    @FXML
    private TextField xstdmc;

    @FXML
    private TextField xsk;

    @FXML
    private TextField gzg;

    @FXML
    private TextField dtg;

    @FXML
    private ColorPicker jjembj;

    @FXML
    private TextField gzk;

    @FXML
    private TextField dtk;

    @FXML
    private ColorPicker zttzq;

    @FXML
    private TextField xsx;

    @FXML
    private ColorPicker embj;

    @FXML
    private TextField xsy;

    @FXML
    private TextField gzx;

    @FXML
    private ColorPicker fsbj;

    @FXML
    private TextField fsbl;

    @FXML
    private TextField gzy;

    @FXML
    private TextField dty;

    @FXML
    private ColorPicker zttyd;

    @FXML
    private TextField dtx;

    @FXML
    private TextField xtg;

    @FXML
    private TextField xtk;

    @FXML
    private ColorPicker hjbh;

    @FXML
    private TextField xty;

    @FXML
    private TextField xtx;
    
    @Resource
    private ConfigsMapper configsMapper;
    
    
    @FXML
    public void initialize(){
        txGroup = new ToggleGroup();
        txGroup.getToggles().addAll(thq, thh, tgh);

        txbzGroup = new ToggleGroup();
        txbzGroup.getToggles().addAll(fkh, ykh, kg, wu);

        jfpGroup = new ToggleGroup();
        jfpGroup.getToggles().addAll(hxfb, zydl, jinyong);
        
        showData();
    }
    
    
    private void showData(){
        final List<Configs> configs = configsMapper.selectList(new LambdaQueryWrapper<>());
        Map<Integer, Configs> configsMap = configs.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        final Configs dati = configsMap.get(1);
        final String config = dati.getConfig();
        if (!StringUtils.isEmpty(config)) {
            final String[] split = config.split(ConfigSpliter.SPLITER);
            tg.setValue(Color.valueOf(split[0]));
            da.setValue(Color.valueOf(split[1]));
            mrzh.setText(split[2]);
            hj.setText(split[3]);
            daqz.setText(split[4]);
            dtx.setText(split[5]);
            dty.setText(split[6]);
            dtk.setText(split[7]);
            dtg.setText(split[8]);
            txGroup.getToggles().get(Integer.parseInt(split[9]) - 1).setSelected(true);
            txbzGroup.getToggles().get(Integer.parseInt(split[10]) - 1).setSelected(true);
            bt.setValue(Color.valueOf(split[11]));
            xsbt.setSelected(Boolean.valueOf(split[12]));
            zh.setText(split[13]);
            jfpGroup.getToggles().get(Integer.parseInt(split[14]) - 1).setSelected(true);
            dm.setValue(Color.valueOf(split[15]));
            dwbj.setValue(Color.valueOf(split[16]));
            dwx.setText(split[17]);
            dwy.setText(split[18]);
            dwk.setText(split[19]);
            dwg.setText(split[20]);

            fs.setValue(Color.valueOf(split[21]));
            fsbj.setValue(Color.valueOf(split[22]));
            djs.setValue(Color.valueOf(split[23]));
            djszh.setText(split[24]);
            zttdd.setValue(Color.valueOf(split[25]));
            zttyd.setValue(Color.valueOf(split[26]));
            zttzq.setValue(Color.valueOf(split[27]));
            zttcw.setValue(Color.valueOf(split[28]));
            djsx.setText(split[29]);
            djsy.setText(split[30]);
        }
        
        final Configs huanjie = configsMap.get(2);
        final String huanjieVal = huanjie.getConfig();
        if (!StringUtils.isEmpty(huanjieVal)) {
            final String[] huanjieArr = huanjieVal.split(ConfigSpliter.SPLITER);
            hjbh.setValue(Color.valueOf(huanjieArr[0]));
            bhbj.setValue(Color.valueOf(huanjieArr[1]));
            hjmc.setValue(Color.valueOf(huanjieArr[2]));
            mcbj.setValue(Color.valueOf(huanjieArr[3]));
            hjx.setText(huanjieArr[4]);
            hjy.setText(huanjieArr[5]);
            hjk.setText(huanjieArr[6]);
            hjg.setText(huanjieArr[7]);
            xscjan.setSelected(Boolean.valueOf(huanjieArr[8]));
            xshjlb.setSelected(Boolean.valueOf(huanjieArr[9]));
        }
        
        
        final Configs paiming = configsMap.get(3);
        final String paimingVal = paiming.getConfig();
        if (!StringUtils.isEmpty(paimingVal)) {
            String[] paimingArr = paimingVal.split(ConfigSpliter.SPLITER);
            bhbl.setText(paimingArr[0]);
            tdmcbl.setText(paimingArr[1]);
            fsbl.setText(paimingArr[2]);
            pmzs.setValue(Color.valueOf(paimingArr[3]));
            pmbj.setValue(Color.valueOf(paimingArr[4]));
            ymbj.setValue(Color.valueOf(paimingArr[5]));
            embj.setValue(Color.valueOf(paimingArr[6]));
            smbj.setValue(Color.valueOf(paimingArr[7]));
            pmx.setText(paimingArr[8]);
            pmy.setText(paimingArr[9]);
            pmk.setText(paimingArr[10]);
            pmg.setText(paimingArr[11]);
        }
        final Configs xuanti = configsMap.get(4);
        final String xuantiVal = xuanti.getConfig();
        if (!StringUtils.isEmpty(xuantiVal)) {
            final String[] xuantiArr = xuantiVal.split(ConfigSpliter.SPLITER);
            xtzs.setValue(Color.valueOf(xuantiArr[0]));
            xxbj.setValue(Color.valueOf(xuantiArr[1]));
            gdxt.setValue(Color.valueOf(xuantiArr[2]));
            xtx.setText(xuantiArr[3]);
            xty.setText(xuantiArr[4]);
            xtk.setText(xuantiArr[5]);
            xtg.setText(xuantiArr[6]);
        }

        final Configs guize = configsMap.get(5);
        final String guizeVal = guize.getConfig();
        if (!StringUtils.isEmpty(guizeVal)) {
            final String[] guizeArr = guizeVal.split(ConfigSpliter.SPLITER);
            gzzs.setValue(Color.valueOf(guizeArr[0]));
            gzx.setText(guizeArr[1]);
            gzy.setText(guizeArr[2]);
            gzk.setText(guizeArr[3]);
            gzg.setText(guizeArr[4]);
        }

        final Configs qiangda = configsMap.get(6);
        final String qiangdaVal = qiangda.getConfig();
        if (!StringUtils.isEmpty(qiangdaVal)) {
            final String[] qiangdaArr = qiangdaVal.split(ConfigSpliter.SPLITER);
            qdbhbl.setText(qiangdaArr[0]);
            qdtdmc.setText(qiangdaArr[1]);
            qdsj.setText(qiangdaArr[2]);
            qdzs.setValue(Color.valueOf(qiangdaArr[3]));
            qdbj.setValue(Color.valueOf(qiangdaArr[4]));
            qdymbj.setValue(Color.valueOf(qiangdaArr[5]));
            qdembj.setValue(Color.valueOf(qiangdaArr[6]));
            qdsmbj.setValue(Color.valueOf(qiangdaArr[7]));
            qdx.setText(qiangdaArr[8]);
            qdy.setText(qiangdaArr[8]);
            qdk.setText(qiangdaArr[8]);
            qdg.setText(qiangdaArr[8]);
        }
        final Configs xuanshou = configsMap.get(7);
        final String xuanshouVal = xuanshou.getConfig();
        if (!StringUtils.isEmpty(xuanshouVal)) {
            final String[] xuanshouArr = xuanshouVal.split(ConfigSpliter.SPLITER);
            xsbhbl.setText(xuanshouArr[0]);
            xstdmc.setText(xuanshouArr[1]);
            xsxs.setText(xuanshouArr[2]);
            xsfs.setText(xuanshouArr[3]);
            xszs.setValue(Color.valueOf(xuanshouArr[4]));
            xsbj.setValue(Color.valueOf(xuanshouArr[5]));
            xsymbj.setValue(Color.valueOf(xuanshouArr[6]));
            xsembj.setValue(Color.valueOf(xuanshouArr[7]));
            xssmbj.setValue(Color.valueOf(xuanshouArr[8]));
            xsx.setText(xuanshouArr[9]);
            xsy.setText(xuanshouArr[10]);
            xsk.setText(xuanshouArr[11]);
            xsg.setText(xuanshouArr[12]);
        }
        
        final Configs jingji = configsMap.get(8);
        final String jingjiVal = jingji.getConfig();
        if (!StringUtils.isEmpty(jingjiVal)) {
            final String[] jingjiArr = jingjiVal.split(ConfigSpliter.SPLITER);
            jjbhbl.setText(jingjiArr[0]);
            jjtdmc.setText(jingjiArr[1]);
            jjfs.setText(jingjiArr[2]);
            jjsj.setText(jingjiArr[3]);
            jjzs.setValue(Color.valueOf(jingjiArr[4]));
            jjbj.setValue(Color.valueOf(jingjiArr[5]));
            jjymbj.setValue(Color.valueOf(jingjiArr[6]));
            jjembj.setValue(Color.valueOf(jingjiArr[7]));
            jjsmbj.setValue(Color.valueOf(jingjiArr[8]));
            jjx.setText(jingjiArr[9]);
            jjy.setText(jingjiArr[10]);
            jjk.setText(jingjiArr[11]);
            jjg.setText(jingjiArr[12]);
        }

    }
    
    @FXML
    public void updateConfig(){
        // 答题  ------------------------------------------------
        final String tigan = tg.getValue().toString();
        final String daan = da.getValue().toString();
        final String morenzihao = mrzh.getText();
        final String hangju = hj.getText();
        final String daanqianzhui = daqz.getText();
        final String datix = dtx.getText();
        final String datiy = dty.getText();
        final String datikuan = dtk.getText();
        final String datigao = dtg.getText();
        final String tixing = txGroup.getSelectedToggle().getUserData().toString();
        final String tixingbiaozhu = txbzGroup.getSelectedToggle().getUserData().toString();
        final String biaotiyanse = bt.getValue().toString();
        final boolean xianshibiaoti = xsbt.isSelected();
        final String biaotizihao = zh.getText();
        final String jifenpaifenbu = jfpGroup.getSelectedToggle().getUserData().toString();
        final String dwmc = dm.getValue().toString();
        final String duimingbeijing = dwbj.getValue().toString();
        final String duiwux = this.dwx.getText();
        final String duiwuy = this.dwy.getText();
        final String duiwukuan = this.dwk.getText();
        final String duiwugao = this.dwg.getText();
        final String fenshuyanse = fs.getValue().toString();
        final String fenshubeijing = fsbj.getValue().toString();
        final String daojishibeijing = djs.getValue().toString();
        final String daojishizihao = djszh.getText();
        final String zhuangtaitiaodengdai = zttdd.getValue().toString();
        final String zhuangtaitiaoyida = zttyd.getValue().toString();
        final String zhuangtaitiaozhengque = zttzq.getValue().toString();
        final String zhuangtaitiaocuowu = zttcw.getValue().toString();
        final String daojishix = djsx.getText();
        final String daojishiy = djsy.getText();
        
        String datiValue = (!StringUtils.isEmpty(tigan) ? tigan: ConfigSpliter.DIFFER) + ConfigSpliter.SPLITER
                + (!StringUtils.isEmpty(daan)? daan: ConfigSpliter.DIFFER)  + ConfigSpliter.SPLITER
                + (!StringUtils.isEmpty(morenzihao)? morenzihao: ConfigSpliter.DIFFER)  + ConfigSpliter.SPLITER
                + (!StringUtils.isEmpty(hangju)? hangju: ConfigSpliter.DIFFER)  + ConfigSpliter.SPLITER
                + daanqianzhui  + ConfigSpliter.SPLITER
                + datix  + ConfigSpliter.SPLITER
                + datiy  + ConfigSpliter.SPLITER
                + datikuan  + ConfigSpliter.SPLITER
                + datigao  + ConfigSpliter.SPLITER
                + tixing  + ConfigSpliter.SPLITER
                + tixingbiaozhu  + ConfigSpliter.SPLITER
                + biaotiyanse  + ConfigSpliter.SPLITER
                + xianshibiaoti  + ConfigSpliter.SPLITER
                + biaotizihao  + ConfigSpliter.SPLITER
                + jifenpaifenbu  + ConfigSpliter.SPLITER
                + dwmc  + ConfigSpliter.SPLITER
                + duimingbeijing  + ConfigSpliter.SPLITER
                + duiwux  + ConfigSpliter.SPLITER
                + duiwuy  + ConfigSpliter.SPLITER
                + duiwukuan  + ConfigSpliter.SPLITER
                + duiwugao  + ConfigSpliter.SPLITER
                + fenshuyanse  + ConfigSpliter.SPLITER
                + fenshubeijing  + ConfigSpliter.SPLITER
                + daojishibeijing  + ConfigSpliter.SPLITER
                + daojishizihao  + ConfigSpliter.SPLITER
                + zhuangtaitiaodengdai  + ConfigSpliter.SPLITER
                + zhuangtaitiaoyida  + ConfigSpliter.SPLITER
                + zhuangtaitiaozhengque  + ConfigSpliter.SPLITER
                + zhuangtaitiaocuowu  + ConfigSpliter.SPLITER
                + daojishix  + ConfigSpliter.SPLITER
                + daojishiy ;

        // 环节  ------------------------------------------------
        final String huanjiebianhaoyanse = hjbh.getValue().toString();
        final String bianhaobeijing = bhbj.getValue().toString();
        final String huanjiemingchengyanse = hjmc.getValue().toString();
        final String mingchengbeijing = mcbj.getValue().toString();
        final String huanjiex = hjx.getText();
        final String huanjiey = hjy.getText();
        final String huanjiekuan = hjk.getText();
        final String huanjiegao = hjg.getText();
        final boolean xianshichoujianganniu = xscjan.isSelected();
        final boolean xianshihuanjieliebiao = xshjlb.isSelected();
        
        String huanjieVal = huanjiebianhaoyanse + ConfigSpliter.SPLITER
                + bianhaobeijing + ConfigSpliter.SPLITER
                + huanjiemingchengyanse + ConfigSpliter.SPLITER
                + mingchengbeijing + ConfigSpliter.SPLITER
                + huanjiex + ConfigSpliter.SPLITER
                + huanjiey + ConfigSpliter.SPLITER
                + huanjiekuan + ConfigSpliter.SPLITER
                + huanjiegao + ConfigSpliter.SPLITER
                + xianshichoujianganniu + ConfigSpliter.SPLITER
                + xianshihuanjieliebiao;

        // 排名  ------------------------------------------------
        final String bianhaobili = bhbl.getText();
        final String tuanduimingchengbili = tdmcbl.getText();
        final String fenshubili = fsbl.getText();
        final String paimingzise = pmzs.getValue().toString();
        final String paimingbeijing = pmbj.getValue().toString();
        final String paimingyimingbeijing = ymbj.getValue().toString();
        final String paimingermingbeijing = embj.getValue().toString();
        final String paimingsanmingbeijing = smbj.getValue().toString();
        final String paimingx = pmx.getText();
        final String paimingy = pmy.getText();
        final String paimingkuan = pmk.getText();
        final String paiminggao = pmg.getText();
        
        String paimingVal = bianhaobili + ConfigSpliter.SPLITER
                + tuanduimingchengbili + ConfigSpliter.SPLITER
                + fenshubili + ConfigSpliter.SPLITER
                + paimingzise + ConfigSpliter.SPLITER
                + paimingbeijing + ConfigSpliter.SPLITER
                + paimingyimingbeijing + ConfigSpliter.SPLITER
                + paimingermingbeijing + ConfigSpliter.SPLITER
                + paimingsanmingbeijing + ConfigSpliter.SPLITER
                + paimingx + ConfigSpliter.SPLITER
                + paimingy + ConfigSpliter.SPLITER
                + paimingkuan + ConfigSpliter.SPLITER
                + paiminggao;
                

        // 选题  ------------------------------------------------
        final String xuantizise = xtzs.getValue().toString();
        final String xuanxiangbeijing = xxbj.getValue().toString();
        final String gundongxuanti = gdxt.getValue().toString();
        final String xuantix = xtx.getText();
        final String xuantiy = xty.getText();
        final String xuantikuan = xtk.getText();
        final String xuantigao = xtg.getText();
        
        String xuantiVal = xuantizise + ConfigSpliter.SPLITER
                + xuanxiangbeijing + ConfigSpliter.SPLITER
                + gundongxuanti + ConfigSpliter.SPLITER
                + xuantix + ConfigSpliter.SPLITER
                + xuantiy + ConfigSpliter.SPLITER
                + xuantikuan + ConfigSpliter.SPLITER
                + xuantigao ;

        // 规则  ------------------------------------------------

        final String guizezise = gzzs.getValue().toString();
        final String guizex = gzx.getText();
        final String guizey = gzy.getText();
        final String guizekuan = gzk.getText();
        final String guizegao = gzg.getText();
        
        String guizeVal = guizezise + ConfigSpliter.SPLITER
                + guizex + ConfigSpliter.SPLITER
                + guizey + ConfigSpliter.SPLITER
                + guizekuan + ConfigSpliter.SPLITER
                + guizegao ;
        
        // 抢答  ------------------------------------------------
        final String qiangdabianhaobili = qdbhbl.getText();
        final String qiangdatuanduimingcheng = qdtdmc.getText();
        final String qiangdashijian = qdsj.getText();
        final String qiangdazise = qdzs.getValue().toString();
        final String qiangdabeijing = qdbj.getValue().toString();
        final String qiangdayimingbeijing = qdymbj.getValue().toString();
        final String qiangdaermingbeijing = qdembj.getValue().toString();
        final String qiangdasanmingbeijing = qdsmbj.getValue().toString();
        final String qiangdax = qdx.getText();
        final String qiangday = qdy.getText();
        final String qiangdakuan = qdk.getText();
        final String qiangdagao = qdg.getText();
        
        String qiangdaVal = qiangdabianhaobili + ConfigSpliter.SPLITER
                + qiangdatuanduimingcheng + ConfigSpliter.SPLITER
                + qiangdashijian + ConfigSpliter.SPLITER
                + qiangdazise + ConfigSpliter.SPLITER
                + qiangdabeijing + ConfigSpliter.SPLITER
                + qiangdayimingbeijing + ConfigSpliter.SPLITER
                + qiangdaermingbeijing + ConfigSpliter.SPLITER
                + qiangdasanmingbeijing + ConfigSpliter.SPLITER
                + qiangdax + ConfigSpliter.SPLITER
                + qiangday + ConfigSpliter.SPLITER
                + qiangdakuan + ConfigSpliter.SPLITER
                + qiangdagao ;
        // 选手  ------------------------------------------------

        final String xuanshoubianhaobli = xsbhbl.getText();
        final String xuanshoutuanduimingcheng = xstdmc.getText();
        final String xuanshoumingcheng = xsxs.getText();
        final String xuanshoufenshu = xsfs.getText();
        final String xuanshouzise = xszs.getValue().toString();
        final String xuanshoubeijing = xsbj.getValue().toString();
        final String xuanshouyimingbeijing = xsymbj.getValue().toString();
        final String xuanshouermingbeijing = xsembj.getValue().toString();
        final String xuanshousanmingbeijing = xssmbj.getValue().toString();
        final String xuanshoux = xsx.getText();
        final String xuanshouy = xsy.getText();
        final String xuanshoukuan = xsk.getText();
        final String xuanshougao = xsg.getText();
        
        String xuanshouVal = xuanshoubianhaobli + ConfigSpliter.SPLITER
                + xuanshoutuanduimingcheng + ConfigSpliter.SPLITER
                + xuanshoumingcheng + ConfigSpliter.SPLITER
                + xuanshoufenshu + ConfigSpliter.SPLITER
                + xuanshouzise + ConfigSpliter.SPLITER
                + xuanshoubeijing + ConfigSpliter.SPLITER
                + xuanshouyimingbeijing + ConfigSpliter.SPLITER
                + xuanshouermingbeijing + ConfigSpliter.SPLITER
                + xuanshousanmingbeijing + ConfigSpliter.SPLITER
                + xuanshoux + ConfigSpliter.SPLITER
                + xuanshouy + ConfigSpliter.SPLITER
                + xuanshoukuan + ConfigSpliter.SPLITER
                + xuanshougao ;
        // 竞技  ------------------------------------------------
        final String jingjibianhaobili = jjbhbl.getText();
        final String jingjituanduimingcheng = jjtdmc.getText();
        final String jingjifenshu = jjfs.getText();
        final String jingjishijian = jjsj.getText();
        final String jingjizise = jjzs.getValue().toString();
        final String jingjibeijing = jjbj.getValue().toString();
        final String jingjiyimingbeijing = jjymbj.getValue().toString();
        final String jingjiermingbeijing = jjembj.getValue().toString();
        final String jingjisanmingbeijing = jjsmbj.getValue().toString();
        final String jingjix = jjx.getText();
        final String jingjiy = jjy.getText();
        final String jingjikuan = jjk.getText();
        final String jingjigao = jjg.getText();
        
        String jingjiVal = jingjibianhaobili + ConfigSpliter.SPLITER
                + jingjituanduimingcheng + ConfigSpliter.SPLITER
                + jingjifenshu + ConfigSpliter.SPLITER
                + jingjishijian + ConfigSpliter.SPLITER
                + jingjizise + ConfigSpliter.SPLITER
                + jingjibeijing + ConfigSpliter.SPLITER
                + jingjiyimingbeijing + ConfigSpliter.SPLITER
                + jingjiermingbeijing + ConfigSpliter.SPLITER
                + jingjisanmingbeijing + ConfigSpliter.SPLITER
                + jingjix + ConfigSpliter.SPLITER
                + jingjiy + ConfigSpliter.SPLITER
                + jingjikuan + ConfigSpliter.SPLITER
                + jingjigao;

        // 保存到数据库
        Configs dati = new Configs();
        dati.setId(1);
        dati.setConfig(datiValue);
        
        configsMapper.updateById(dati);
        
        Configs huanjie = new Configs();
        huanjie.setId(2);
        huanjie.setConfig(huanjieVal);
        configsMapper.updateById(huanjie);

        Configs paiming = new Configs();
        paiming.setId(3);
        paiming.setConfig(paimingVal);
        configsMapper.updateById(paiming);

        Configs xuanti = new Configs();
        xuanti.setId(4);
        xuanti.setConfig(xuantiVal);
        configsMapper.updateById(xuanti);

        Configs guize = new Configs();
        guize.setId(5);
        guize.setConfig(guizeVal);
        configsMapper.updateById(guize);

        Configs qiangda = new Configs();
        qiangda.setId(6);
        qiangda.setConfig(qiangdaVal);
        configsMapper.updateById(qiangda);

        Configs xuanshou = new Configs();
        xuanshou.setId(7);
        xuanshou.setConfig(xuanshouVal);
        configsMapper.updateById(xuanshou);

        Configs jingji = new Configs();
        jingji.setId(8);
        jingji.setConfig(jingjiVal);
        configsMapper.updateById(jingji);

        controlPane.updateScoreBoard();
        ReloadPane timeBoard = controlPane.getTimeBoardPreview();
        if (timeBoard != null) {
            timeBoard.reload();
        }
        PreviewStack.reload();
    }
    
}
