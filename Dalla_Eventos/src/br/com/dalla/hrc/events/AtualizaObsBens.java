package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.comercial.ImportacaoXmlNotaCompraHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class AtualizaObsBens implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {
        atualizaObsBens(event);
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext ctx) throws Exception {

    }

    private void atualizaObsBens(PersistenceEvent event) throws Exception {
        DynamicVO iBensVO = (DynamicVO) event.getVo();
        BigDecimal nuNota = iBensVO.asBigDecimal("NUNOTA");
        BigDecimal sequencia = iBensVO.asBigDecimal("SEQUENCIA");
        BigDecimal codProd = iBensVO.asBigDecimal("CODPROD");
        String codBem = iBensVO.asString("CODBEM");
       // String atualBem = iBensVO.asString("ATUALBEM");

        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        DynamicVO bensVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("Imobilizado", new Object[]{codBem , codProd});
        String nomeBem = bensVO.asString("DESCRABREV");

        PersistentLocalEntity LocalEntity = dwfFacade.findEntityByPrimaryKey("ItemNota", new Object[]{nuNota , sequencia});
        EntityVO NVO = LocalEntity.getValueObject();
        DynamicVO iteVO = (DynamicVO) NVO;

        iteVO.setProperty("OBSERVACAO", nomeBem);
        LocalEntity.setValueObject(NVO);

        

    }

    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }

}
