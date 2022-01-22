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
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class IncluirNotasColetas implements EventoProgramavelJava {
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

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {


        incluirNotasColetas(event);
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext ctx) throws Exception {

    }
    private void incluirNotasColetas(PersistenceEvent event) throws Exception {
        DynamicVO cabVO = (DynamicVO) event.getVo();
        BigDecimal nuNota = cabVO.asBigDecimal("NUNOTA");
        BigDecimal numNota = cabVO.asBigDecimal("NUMNOTA");
        BigDecimal codEmp = cabVO.asBigDecimal("CODEMP");
        BigDecimal codtipoper = cabVO.asBigDecimal("CODTIPOPER");
        Timestamp dtNeg = cabVO.asTimestamp("DTNEG");
        BigDecimal codTransportadora = cabVO.asBigDecimal("CODPARCTRANSP");
        BigDecimal codParc = cabVO.asBigDecimal("CODPARC");
        BigDecimal vlrNota = cabVO.asBigDecimal("VLRNOTA");
        BigDecimal vlrFrete = cabVO.asBigDecimal("VLRFRETE");
        String statusNfe = (String) cabVO.getProperty("STATUSNFE");


        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();

        NativeSql sql = new NativeSql(jdbc);
        sql.setReuseStatements(true);
        sql.setNamedParameter("NUNOTA",nuNota );
        sql.appendSql(" SELECT ");
        sql.appendSql(" COUNT(1) AS QTD ,AD_CODEMPDEST AS CODEMPDEST");
        sql.appendSql(" FROM TGFITE ITE ");
        sql.appendSql(" WHERE NUNOTA = :NUNOTA");
        sql.appendSql(" GROUP BY AD_CODEMPDEST");
        sql.appendSql(" HAVING count(1) > 0");

        ResultSet rset = sql.executeQuery();
        while (rset.next()) {
            BigDecimal codempdest = rset.getBigDecimal("CODEMPDEST");

            ArrayList<BigDecimal> listaEmpresas = new ArrayList<>(Arrays.asList(new BigDecimal(2),new BigDecimal(9),new BigDecimal(12)));
            ArrayList<BigDecimal> listaTops = new ArrayList<>(Arrays.asList(new BigDecimal(1108),new BigDecimal(1154)));

            if (listaEmpresas.contains(codEmp) && listaTops.contains(codtipoper)&& statusNfe != null)  {

                if (statusNfe.equals("A")) {
                    EntityVO colVO = dwfFacade.getDefaultValueObjectInstance("AD_TDHCOL");
                    DynamicVO newColVO = (DynamicVO) colVO;

                    newColVO.setProperty("NUNOTA", nuNota);
                    newColVO.setProperty("NUMNOTA", numNota);
                    newColVO.setProperty("CODPARCTRANSP", codTransportadora);
                    newColVO.setProperty("CODPARC", codParc);
                    newColVO.setProperty("VLRNOTA", vlrNota);
                    newColVO.setProperty("VLRFRETE", vlrFrete);
                    newColVO.setProperty("CODEMP", codempdest);
                    newColVO.setProperty("DTNEG", dtNeg);
                    newColVO.setProperty("COLETAR", "A");
                    //newColVO.setProperty("MOTORISTA", null);
                    dwfFacade.createEntity("AD_TDHCOL", (EntityVO) newColVO);
                }
            }
        }
    }




    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }
}
