package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.comercial.LiberacaoAlcadaHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

public class validaPromo implements EventoProgramavelJava {
    private DynamicVO iteVO;
    private DynamicVO promVO;
    private BigDecimal nuNota;
    private BigDecimal sequencia;
    private EntityFacade dwfFacade;
    private BigDecimal codProd;
    private BigDecimal descCodProd;
    private Date dataFinal = null;
    private Date dataNeg = null;

    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    public void afterInsert(PersistenceEvent event) throws Exception {
        validarPromocao(event);
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        validarPromocao(event);
    }

    @Override
    public void beforeCommit(TransactionContext event) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        validarPromocao(event);

    }
    public void validarPromocao(PersistenceEvent event) throws Exception {

        JdbcWrapper jdbcWrapper = null;

        DynamicVO iteVO = (DynamicVO) event.getVo();
        BigDecimal nuNota = (BigDecimal) iteVO.asBigDecimal("NUNOTA");
        BigDecimal sequencia = (BigDecimal) iteVO.asBigDecimal("SEQUENCIA");
        BigDecimal codProd = (BigDecimal) iteVO.asBigDecimal("CODPROD");

        JdbcWrapper jdbc = null;
        EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        jdbc = dwf.getJdbcWrapper();
        jdbc.openSession();

        NativeSql sqlProd = new NativeSql(jdbc);
        sqlProd.setReuseStatements(true);
        sqlProd.appendSql(" SELECT ");
        sqlProd.appendSql(" D.CODPROD AS CODPROD");
        sqlProd.appendSql(" FROM AD_TGFDESC D ");
        sqlProd.appendSql(" WHERE  D.CODPROD = :CODPROD");
        sqlProd.setNamedParameter("CODPROD", codProd);
        ResultSet rProd = sqlProd.executeQuery();
        BigDecimal resultadoProd = null;

        while (rProd.next()) {
            resultadoProd = rProd.getBigDecimal("CODPROD");
        }
        if (resultadoProd != null) {

            DynamicVO dscVO = this.buscaProdutoPromo(codProd);
            DynamicVO cabVO = this.buscaCab(nuNota);
            BigDecimal codTop = (BigDecimal) cabVO.getProperty("CODTIPOPER");
            DynamicVO topVO = ComercialUtils.getTipoOperacao(codTop);
            BigDecimal vlrDesc = (BigDecimal) iteVO.getProperty("VLRDESC");
            BigDecimal percDesc = (BigDecimal) iteVO.getProperty("PERCDESC");
            String validaTopComercial = (String) topVO.asString("AD_PEDCOMERCIAL");
            BigDecimal cabCodEmp = (BigDecimal) cabVO.asBigDecimal("CODEMP");
            BigDecimal codTipVenda = (BigDecimal) cabVO.asBigDecimal("CODTIPVENDA");

            EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            jdbcWrapper.openSession();

            Timestamp dthoje = new Timestamp(new Date().getTime());

            NativeSql sql = new NativeSql(jdbcWrapper);
            sql.setReuseStatements(true);
            sql.appendSql(" SELECT ");
            sql.appendSql(" P.CODGRUPODESC AS RESULTADO");
            sql.appendSql(" FROM AD_PROMOCOES P, AD_TGFDESC D, AD_TSIEPP EMP, AD_TGFTPVP TPV ");
            sql.appendSql(" WHERE  D.CODPROD = :CODPROD AND P.DTFIN >= :DTHOJE AND EMP.CODEMP = :CODEMP AND P.CODGRUPODESC = EMP.CODGRUPODESC AND P.CODGRUPODESC = D.CODGRUPODESC AND TPV.CODGRUPODESC = P.CODGRUPODESC AND TPV.CODTIPVENDA = :CODTIPVENDA ");
            sql.setNamedParameter("CODPROD", codProd);
            sql.setNamedParameter("DTHOJE", dthoje);
            sql.setNamedParameter("CODEMP", cabCodEmp);
            sql.setNamedParameter("CODTIPVENDA", codTipVenda);
            ResultSet rset = sql.executeQuery();
            BigDecimal resultado = null;

            while (rset.next()) {
                resultado = rset.getBigDecimal("RESULTADO");
            }
            if (resultado !=null) {

                    LiberacaoAlcadaHelper.inserirSolicitacao();

                if (resultado.intValue() > 0 && vlrDesc.doubleValue() > 0 && percDesc.doubleValue() > 0 && validaTopComercial.equals("S")) {
                    exibirErro("Produto está na tabela de promoção: " + resultado + "\nNão é possível dar desconto em um produto em promoção!");
                }
                jdbcWrapper.closeSession();;
            }
        }
    }

    private DynamicVO  buscaProdutoPromo (BigDecimal codProd) throws Exception {
        JapeWrapper DAO =JapeFactory.dao("AD_TGFDESC");
        DynamicVO VO = DAO.findOne("CODPROD=?", new Object [] {codProd});
        return VO;
    }

    private DynamicVO  buscaPromo (BigDecimal codGrupoDesc) throws Exception {
        JapeWrapper DAO =JapeFactory.dao("AD_PROMOCOES");

        DynamicVO VO = (DynamicVO) DAO.findOne("CODGRUPODESC=?", new Object [] {codGrupoDesc} );
        return VO;
    }

    private DynamicVO  buscaCab (BigDecimal nroUnico) throws Exception {
        JapeWrapper DAO =JapeFactory.dao("CabecalhoNota");
        DynamicVO VO = DAO.findOne("NUNOTA=?", new Object [] {nroUnico});
        return VO;
    }

    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }

}