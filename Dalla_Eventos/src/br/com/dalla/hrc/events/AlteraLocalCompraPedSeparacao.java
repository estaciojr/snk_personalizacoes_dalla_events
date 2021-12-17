package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
//import com.ibm.icu.math.BigDecimal;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;

public class AlteraLocalCompraPedSeparacao implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        start(persistenceEvent);
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
    private void start(PersistenceEvent persistenceEvent)  throws Exception {

        DynamicVO cabVO = (DynamicVO)persistenceEvent.getVo();

        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

        BigDecimal nuNota = cabVO.asBigDecimal("NUNOTA");
        String tipMov = cabVO.asString("TIPMOV");
        BigDecimal codtipoper = cabVO.asBigDecimal("CODTIPOPER");
        //BigDecimal localPadrao = BigDecimal.valueOf(104L);
        BigDecimal localAlteracao = BigDecimal.valueOf(102L);
        BigDecimal localEntrada  = BigDecimal.valueOf(105L);
        BigDecimal topCompra = BigDecimal.valueOf(1430L);
        int situacaoWms = cabVO.asInt("SITUACAOWMS");



        Iterator<?> itepro;
        if ((!tipMov.isEmpty()) &&
                (tipMov.equals("C")) && (codtipoper.compareTo(topCompra) == 0) && (situacaoWms == 16 ))
        {

           // this.exibirErro("Situação WMS: " + situacaoWms);

            Collection<?> iteproduto = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA>0 ", new Object[] { nuNota }));
            if (!iteproduto.isEmpty()) {
                for (itepro = iteproduto.iterator(); itepro.hasNext();)
                {
                    PersistentLocalEntity iteproEntity = (PersistentLocalEntity)itepro.next();
                    ItemNotaVO iteproVO = (ItemNotaVO)((DynamicVO)iteproEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
                    BigDecimal qtdnegCompra = (BigDecimal)iteproVO.getProperty("QTDNEG");
                    if (iteproVO.getCODLOCALORIG().equals(localEntrada))
                    {
                        BigDecimal codprod = (BigDecimal)iteproVO.getProperty("CODPROD");
                        BigDecimal contador = qtdnegCompra;

                        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
                        NativeSql sql = new NativeSql(jdbc);
                        sql.setReuseStatements(true);
                        sql.appendSql(" SELECT ");
                        sql.appendSql(" CAB.AD_NUNOTAORIG,CAB.NUNOTA,CAB.TIPMOV,CAB.CODTIPOPER,CAB.DTNEG,ITE.CODPROD,ITE.CODLOCALORIG,ITE.QTDNEG,ITE.SEQUENCIA");
                        sql.appendSql(" FROM TGFCAB CAB ");
                        sql.appendSql(" INNER JOIN TGFITE ITE ON ITE.NUNOTA = CAB.NUNOTA ");
                        sql.appendSql(" WHERE ITE.CODLOCALORIG = 104 AND CAB.CODTIPOPER  = 1063 AND ITE.CODPROD = :CODPROD ");
                        sql.appendSql(" ORDER BY CAB.DTNEG ASC");
                        sql.setNamedParameter("CODPROD", codprod);
                        ResultSet rset = sql.executeQuery();
                        while (rset.next())
                        {
                            BigDecimal nunota = rset.getBigDecimal("NUNOTA");

                            Collection<?> iteprodutoped = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA>0 ", new Object[] { nunota }));
                            Iterator<?> iteproped;
                            if (!iteprodutoped.isEmpty()) {
                                for (iteproped = iteprodutoped.iterator(); iteproped.hasNext();)
                                {
                                    PersistentLocalEntity itepropedEntity = (PersistentLocalEntity)iteproped.next();
                                    ItemNotaVO itepropedVO = (ItemNotaVO)((DynamicVO)itepropedEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
                                    if ((itepropedVO.getCODLOCALORIG().compareTo(localEntrada) == 0) && (itepropedVO.getCODPROD().compareTo(codprod) == 0) && ((contador.compareTo(itepropedVO.getQTDNEG()) == 1) || (contador.compareTo(itepropedVO.getQTDNEG()) == 0))) {
                                        if (contador.compareTo(itepropedVO.getQTDNEG()) == 1)
                                        {
                                            itepropedVO.setProperty("CODLOCALORIG", localAlteracao);
                                            itepropedEntity.setValueObject(itepropedVO);
                                            contador = contador.subtract(itepropedVO.getQTDNEG());
                                        }
                                        else if (contador.compareTo(itepropedVO.getQTDNEG()) == 0)
                                        {
                                            itepropedVO.setProperty("CODLOCALORIG", localAlteracao);
                                            itepropedEntity.setValueObject(itepropedVO);
                                            contador = contador.subtract(itepropedVO.getQTDNEG());
                                        }
                                    }
                                }
                            }
                            iteproVO.setProperty("CODLOCALORIG", localAlteracao);
                            iteproEntity.setValueObject(iteproVO);
                        }
                    }
                }
            }
        }
    }

    private void exibirErro(String mensagem)
            throws Exception
    {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }

    private DynamicVO retornaIte(BigDecimal nuNota)
            throws Exception
    {
        JapeWrapper DAO = JapeFactory.dao("ItemNota");
        DynamicVO VO = DAO.findOne("NUNOTA=?", new Object[] { nuNota });
        return VO;
    }
}

