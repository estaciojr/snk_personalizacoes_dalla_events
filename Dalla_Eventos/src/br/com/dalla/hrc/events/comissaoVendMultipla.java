package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class comissaoVendMultipla implements EventoProgramavelJava {

    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    public void afterInsert(PersistenceEvent event) throws Exception {
        IncluiVendedorMultiplo(event);
    }

    public void afterUpdate(PersistenceEvent event) throws Exception {

    }

    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    public void beforeCommit(TransactionContext event) throws Exception {

    }

    private void IncluiVendedorMultiplo(PersistenceEvent event) throws Exception {

        DynamicVO cabVo = (DynamicVO) event.getVo();
        BigDecimal nuNota = cabVo.asBigDecimal("NUNOTA");
        BigDecimal codtipoper = (BigDecimal) cabVo.getProperty("CODTIPOPER");
       // BigDecimal codTop = (BigDecimal) ComercialUtils.getTipoOperacao(codtipoper);
        BigDecimal codVendTec = cabVo.asBigDecimal("CODVENDTEC");

        if (codVendTec !=null) {
          //  this.exibirErro("o vendedor tecnico é : " + codVendTec);
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        DynamicVO vendVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("Vendedor",codVendTec);
        BigDecimal perCom = vendVO.asBigDecimal("COMVENDA");

        DynamicVO topVO = (DynamicVO) ComercialUtils.getTipoOperacao(codtipoper);
        String atualCom = topVO.asString("ATUALCOM");

        if (atualCom.equals("C")) {
            EntityVO comMultVO = dwfFacade.getDefaultValueObjectInstance(DynamicEntityNames.COMISSAO_MULTIPLA);
            DynamicVO newComMultVO = (DynamicVO) comMultVO;

            newComMultVO.setProperty("NUNOTA", nuNota);
            newComMultVO.setProperty("CODVEND", codVendTec);
            newComMultVO.setProperty("PERCCOM", perCom);

            dwfFacade.createEntity(DynamicEntityNames.COMISSAO_MULTIPLA, (EntityVO) newComMultVO);
          }
        }
    }

    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }
}

