package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;



public class BuscaCodvend implements EventoProgramavelJava {

    //private EntityFacade dwfFacade;

    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    public void afterInsert(PersistenceEvent event) throws Exception {
        buscaCodvendEcom(event);
    }

    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    public void afterUpdate(PersistenceEvent event) throws Exception {
    }

    public void beforeDelete(PersistenceEvent event) throws Exception {
    }

    public void afterDelete(PersistenceEvent event) throws Exception {
    }

    public void beforeCommit(TransactionContext event) throws Exception {
    }

    private void buscaCodvendEcom(PersistenceEvent event) throws Exception {

    //private String codVendVtex == null;

        DynamicVO cabVO = (DynamicVO) event.getVo();
        BigDecimal nuNota = (BigDecimal) cabVO.asBigDecimal("NUNOTA");
       // BigDecimal nuNotaOrig = (BigDecimal) cabVO.asBigDecimal("AD_NUNOTAORIG");
        BigDecimal codEmp = (BigDecimal) cabVO.getProperty("CODEMP");
        BigDecimal codtipoper = (BigDecimal) cabVO.getProperty("CODTIPOPER");
        String nuPedidoVtex = (String) cabVO.getProperty("AD_PEDIDOECOM");

        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();



        if (codEmp.intValue() == 9 && codtipoper.intValue() == 1009 && nuPedidoVtex != null) {

            DynamicVO vtexVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("VtexPedidos", nuPedidoVtex);
            String codVendVtex = (String) vtexVO.asString("IDAFILIADO");

            JapeWrapper vtexPagDAO = JapeFactory.dao("VtexPedidosPagamentos");
            DynamicVO vtexPagVO = vtexPagDAO.findOne("PEDIDO = ?", nuPedidoVtex);
            BigDecimal vlrJuros = vtexPagVO.asBigDecimalOrZero("VALORJUROS");


            if (codVendVtex == null) {
                codVendVtex = new String("DAL");
            }

            if (codVendVtex.equals("PRS") || codVendVtex.equals("PTR") ){
                codVendVtex = new String("PTR");
            }

            if (codVendVtex.equals("BWW") || codVendVtex.equals("PRB") ){
                codVendVtex = new String("PRB");
            }

            JapeWrapper DAO = JapeFactory.dao("Vendedor");
            DynamicVO vendVO = DAO.findOne("AD_AFILIADOECOM = ?", codVendVtex);
            BigDecimal codVend = (BigDecimal) vendVO.getProperty("CODVEND");

            if (codVend == null || codVend.intValue() == 0) {
                codVend = new BigDecimal(157);

            }

            PersistentLocalEntity LocalEntity = dwfFacade.findEntityByPrimaryKey("CabecalhoNota", new Object[]{nuNota});
            EntityVO NVO = LocalEntity.getValueObject();
            DynamicVO CabVO = (DynamicVO) NVO;

            CabVO.setProperty("CODVEND", codVend);
            CabVO.setProperty("AD_NUNOTAORIG",nuNota);
            CabVO.setProperty("VLRDESTAQUE",vlrJuros);

            LocalEntity.setValueObject(NVO);

        }

    }

    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }
}
