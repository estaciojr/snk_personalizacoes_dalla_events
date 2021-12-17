package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ProductsListenersVtex implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        enviaDadosInvoice(persistenceEvent);
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        enviaDadosInvoice(persistenceEvent);
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }

    private void enviaDadosInvoice(PersistenceEvent event) throws Exception {

        DynamicVO newVO = (DynamicVO) event.getVo();
        String statusNota = newVO.asString("STATUSNOTA");
        String nuPedidoVtex = newVO.asString("AD_PEDIDOECOM");
        BigDecimal codtipoper = newVO.asBigDecimal("CODTIPOPER");
        BigDecimal codEmp = newVO.asBigDecimal("CODEMP");
        String chaveNfe = newVO.asString("CHAVENFE");
        Timestamp dtNeg = newVO.asTimestamp("DTNEG");
        BigDecimal vlrNota = newVO.asBigDecimal("VLRNOTA");
        BigDecimal numNota = newVO.asBigDecimal("NUMNOTA");

        DynamicVO topRVO = ComercialUtils.getTipoOperacao(codtipoper);

        String notaEcom = topRVO.asString("AD_NOTAECOM");

        if (notaEcom == null)  {
            notaEcom = new String("N");
        }

        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

        if (codEmp.intValue() == 9 && notaEcom.equals("S") && nuPedidoVtex != null) {

            PersistentLocalEntity LocalEntityF = dwfEntityFacade.findEntityByPrimaryKey("VtexPedidos", nuPedidoVtex);
            EntityVO NVO = LocalEntityF.getValueObject();
            DynamicVO vtexVO = (DynamicVO) NVO;

            vtexVO.setProperty("CHAVENOTA",chaveNfe );
            vtexVO.setProperty("DATANOTAFISCAL", dtNeg);
            vtexVO.setProperty("VALORNOTAFISCAL", vlrNota);
            vtexVO.setProperty("NOTAFISCAL", numNota);
            LocalEntityF.setValueObject(NVO);

        }
    }

    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }

}

