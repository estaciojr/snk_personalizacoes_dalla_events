package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

public class IncluirItensColeta implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {
        IncluirItensColeta(event);
    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {

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
    private void IncluirItensColeta(PersistenceEvent event) throws Exception {
        DynamicVO colVO = (DynamicVO)event.getVo();
        BigDecimal nuNota = colVO.asBigDecimal("NUNOTA");
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        Collection<?> iteCompraEncomenda = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA>0 ", new Object[]{nuNota}));
        Iterator<?> itepro = iteCompraEncomenda.iterator();
        while(itepro.hasNext()) {
            if (!iteCompraEncomenda.isEmpty()) {
                PersistentLocalEntity iteEncomendaEntity = (PersistentLocalEntity)itepro.next();
                ItemNotaVO iteproEncomendaVO = (ItemNotaVO)((DynamicVO)iteEncomendaEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
                BigDecimal qtdneg = iteproEncomendaVO.getQTDNEG();
                BigDecimal  vlrunit = iteproEncomendaVO.getVLRUNIT();
                BigDecimal vlrtot = iteproEncomendaVO.getVLRTOT();
                BigDecimal codProd =iteproEncomendaVO.getCODPROD();
                BigDecimal sequencia = iteproEncomendaVO.getSEQUENCIA();
                String lote = iteproEncomendaVO.getCONTROLE();
                String codvol = iteproEncomendaVO.getCODVOL();

                EntityVO itensVO = dwfFacade.getDefaultValueObjectInstance("AD_TDHICO");
                DynamicVO newItensVO = (DynamicVO)itensVO;
                newItensVO.setProperty("QTDNEG",qtdneg);
                newItensVO.setProperty("VLRUNIT",vlrunit);
                newItensVO.setProperty("VLRTOT",vlrtot);
                newItensVO.setProperty("SEQITECOL",sequencia);
                newItensVO.setProperty("CODPROD",codProd);
                newItensVO.setProperty("QTDDEVOL",qtdneg);
                newItensVO.setProperty("CONTROLE",lote);
                newItensVO.setProperty("NUNOTA",nuNota);
                newItensVO.setProperty("CODVOL",codvol);
                dwfFacade.createEntity("AD_TDHICO",(EntityVO) newItensVO);
            }
        }
    }


    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }
}
