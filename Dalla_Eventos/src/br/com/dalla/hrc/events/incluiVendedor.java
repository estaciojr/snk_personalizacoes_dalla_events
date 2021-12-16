package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import br.com.sankhya.ws.ServiceContext;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


public class incluiVendedor implements EventoProgramavelJava {

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
        incluirVendedor(event);
        incluirFuncionario(event);
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext event) throws Exception {

    }


    private void incluirVendedor(PersistenceEvent event) throws Exception {

        DynamicVO rpaVO = (DynamicVO) event.getVo();
        String nome = rpaVO.asString("APELIDO");
        BigDecimal codRpa = rpaVO.asBigDecimal("CODRPA");
        String tipVEnd = rpaVO.asString("TIPVEND");
        BigDecimal percCOm = rpaVO.asBigDecimal("COMISSAO");


        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        EntityVO venVO = dwfFacade.getDefaultValueObjectInstance(DynamicEntityNames.VENDEDOR);
        DynamicVO newVendVo = (DynamicVO) venVO;

        if (tipVEnd.equals("A")) {
            percCOm = new BigDecimal("2.5");
        }

        newVendVo.setProperty("CODVEND", getUltimoCodvend());
        newVendVo.setProperty("APELIDO", nome);
        newVendVo.setProperty("TIPVEND", new String("T"));
        newVendVo.setProperty("COMVENDA", percCOm);
        newVendVo.setProperty("CODFORM", new BigDecimal(1));

        dwfFacade.createEntity(DynamicEntityNames.VENDEDOR, (EntityVO) newVendVo);

        PersistentLocalEntity LocalEntity = dwfFacade.findEntityByPrimaryKey("AD_CADRPA", codRpa);
        EntityVO NVO = LocalEntity.getValueObject();
        DynamicVO newRpaVO = (DynamicVO) NVO;

        newRpaVO.setProperty("CODVENDTEC", newVendVo.asBigDecimal("CODVEND"));
        newRpaVO.setProperty("COMISSAO", newVendVo.asBigDecimal("COMVENDA"));
        LocalEntity.setValueObject(NVO);
    }

    private BigDecimal getUltimoCodvend() throws Exception {

        BigDecimal codVend = new java.math.BigDecimal(0);

        JdbcWrapper jdbcV = null;
        EntityFacade dwfFacadeV = EntityFacadeFactory.getDWFFacade();
        jdbcV = dwfFacadeV.getJdbcWrapper();

        NativeSql sql = new NativeSql(jdbcV);
        sql.resetSqlBuf();
        sql.appendSql("SELECT MAX(CODVEND)+1 AS CODVEND FROM TGFVEN");
        ResultSet query = sql.executeQuery();
        while (query.next()) {
            codVend = query.getBigDecimal("CODVEND");
        }
        return codVend;


    }

    private void incluirFuncionario(PersistenceEvent event) throws Exception {
        this.setupContext();

        DynamicVO rpaVO = (DynamicVO) event.getVo();


        BigDecimal codRpa = rpaVO.asBigDecimal("CODRPA");
        LocalDateTime dtAlter = LocalDateTime.now();
        String nomeFunc = rpaVO.asString("NOME");
        String sexo = rpaVO.asString("SEXO");
        String telefone = rpaVO.asString("TELEFONE");
        String celular = rpaVO.asString("CELULAR");
        String identidade = rpaVO.asString("IDENTIDADE");
        String cpf = rpaVO.asString("CPF");
        String pis = rpaVO.asString("PIS");
        Timestamp dtNasc = rpaVO.asTimestamp("DTNASC");
        String eMail = rpaVO.asString("EMAIL");
        BigDecimal codEnd = rpaVO.asBigDecimal("CODEND");
        String numEnd = rpaVO.asString("NUMEND");
        String comPlemento = rpaVO.asString("COMPLEMENTO");
        BigDecimal codBai = rpaVO.asBigDecimal("CODBAI");
        BigDecimal codCid = rpaVO.asBigDecimal("CODCID");
        String cep = rpaVO.asString("CEP");
        String nomePai = rpaVO.asString("NOMEPAI");
        String nomeMae = rpaVO.asString("NOMEMAE");
        Timestamp dtAdm = new Timestamp(System.currentTimeMillis());
        Timestamp dtoptFgts = new Timestamp(System.currentTimeMillis());
        //BigDecimal niVesc = rpaVO.asBigDecimal("NIVESC");

        EntityFacade dwfFacadeF = EntityFacadeFactory.getDWFFacade();
        EntityVO funcVO = dwfFacadeF.getDefaultValueObjectInstance(DynamicEntityNames.FUNCIONARIO);
        DynamicVO newfuncVO = (DynamicVO) funcVO;


        newfuncVO.setProperty("CODEMP", getCodEmpUsuLogado());
        newfuncVO.setProperty("CODFUNC", getCodFuncionario());
        newfuncVO.setProperty("DTALTER", dtAlter);
        newfuncVO.setProperty("NOMEFUNC", nomeFunc);
        newfuncVO.setProperty("SEXO", sexo);
        newfuncVO.setProperty("TELEFONE", telefone);
        newfuncVO.setProperty("CELULAR", celular);
        newfuncVO.setProperty("IDENTIDADE", identidade);
        newfuncVO.setProperty("CPF", cpf);
        newfuncVO.setProperty("PIS", pis);
        newfuncVO.setProperty("DTNASC", dtNasc);
        newfuncVO.setProperty("EMAIL", eMail);
        newfuncVO.setProperty("CODEND", codEnd);
        newfuncVO.setProperty("NUMEND", numEnd);
        newfuncVO.setProperty("COMPLEMENTO", comPlemento);
        newfuncVO.setProperty("CODBAI", codBai);
        newfuncVO.setProperty("CODCID", codCid);
        newfuncVO.setProperty("CEP", cep);
        newfuncVO.setProperty("NOMEPAI", nomePai);
        newfuncVO.setProperty("NOMEMAE", nomeMae);
        newfuncVO.setProperty("DTADM", dtAdm);
        newfuncVO.setProperty("DTOPTFGTS", dtoptFgts);
        newfuncVO.setProperty("NIVESC", new BigDecimal(1));
        newfuncVO.setProperty("CODCARGO", new BigDecimal(208));

        JdbcWrapper jdbcC = null;
        EntityFacade dwfFacadeC = EntityFacadeFactory.getDWFFacade();
        jdbcC = dwfFacadeC.getJdbcWrapper();

        NativeSql sql = new NativeSql(jdbcC);

        sql.setNamedParameter("CODFUNC", getCodFuncionario());
        sql.setNamedParameter("CPF", cpf);
        sql.setNamedParameter("CODEMP",getCodEmpUsuLogado());


        ResultSet rts = sql.executeQuery("SELECT RTRIM(NOMEFUNC) FROM TFPFUN WHERE CODFUNC <> :CODFUNC AND CPF = :CPF AND CODEMP = :CODEMP");
        boolean existiCpfRepetido = rts.next();

        if (existiCpfRepetido) {
            this.exibirErro("CPF ja existe para o funcionario " + rts.getString(1));
        }


        JapeWrapper funcDemDAO = JapeFactory.dao("Funcionario");
        DynamicVO funcDemVO = funcDemDAO.findOne("DTDEM IS NOT NULL AND CPF= ?", cpf);
        if (funcDemVO!= null) {


            Timestamp dtDem = funcDemVO.asTimestamp("DTDEM");


            Timestamp dtAtual = new Timestamp(System.currentTimeMillis());

            Calendar c1 = Calendar.getInstance();
            c1.setTime(dtAtual);
            c1.add(Calendar.MONTH, -20);
            Date UltData = c1.getTime();

            Calendar c2 = Calendar.getInstance();
            c2.setTime(dtDem);
            Date dtDemicao = c2.getTime();


            if (dtDemicao.compareTo(UltData) == 1) {
                this.exibirErro("CPF ja existe para o funcionario Demitido a menos de 20 meses!");
            }
        }
        dwfFacadeF.createEntity(DynamicEntityNames.FUNCIONARIO, (EntityVO) newfuncVO);

        PersistentLocalEntity LocalEntityF = dwfFacadeF.findEntityByPrimaryKey("AD_CADRPA", codRpa);
        EntityVO NVOf = LocalEntityF.getValueObject();
        DynamicVO nRpaVO = (DynamicVO) NVOf;

        nRpaVO.setProperty("CODFUNC", getCodFuncionario());
        nRpaVO.setProperty("CODEMP", getCodEmpUsuLogado());
        LocalEntityF.setValueObject(NVOf);

    }

    private BigDecimal getCodFuncionario() throws Exception {

        BigDecimal codFunc = BigDecimal.ZERO;

        JdbcWrapper jdbcF = null;
        EntityFacade dwfFacadeF = EntityFacadeFactory.getDWFFacade();
        jdbcF = dwfFacadeF.getJdbcWrapper();

        NativeSql sql = new NativeSql(jdbcF);
        sql.resetSqlBuf();
        sql.appendSql("SELECT ISNULL(MAX(CODFUNC),90000)+1 AS CODFUNC FROM TFPFUN WHERE CODFUNC BETWEEN 90000 AND 91000");
        ResultSet query = sql.executeQuery();
        while (query.next()) {
            codFunc = query.getBigDecimal("CODFUNC");
        }
        return codFunc;
    }

    private BigDecimal getCodEmpUsuLogado() throws Exception {
        BigDecimal codEmp = new java.math.BigDecimal(0);

        JdbcWrapper jdbcE = null;
        EntityFacade dwfFacadeE = EntityFacadeFactory.getDWFFacade();
        jdbcE = dwfFacadeE.getJdbcWrapper();

        NativeSql sql = new NativeSql(jdbcE);
        sql.resetSqlBuf();
        sql.appendSql("SELECT CODEMP AS CODEMP FROM TSIUSU WHERE CODUSU = :CODUSU");
        sql.setNamedParameter("CODUSU", getUsuLogado());
        ResultSet query = sql.executeQuery();
        while (query.next()) {
            codEmp = query.getBigDecimal("CODEMP");
        }
        return codEmp;
    }


    private BigDecimal getUsuLogado() {
        BigDecimal codUsuLogado = BigDecimal.ZERO;
        codUsuLogado = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();
        return codUsuLogado;
    }


    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }


    public void setupContext() {
        AuthenticationInfo auth = AuthenticationInfo.getCurrent();
        JapeSessionContext.putProperty("usuario_logado", auth.getUserID());
        JapeSessionContext.putProperty("authInfo", auth);
        //JapeSessionContext.putProperty("br.com.sankhya.cadastro.funcionarios", Boolean.TRUE);
        JapeSessionContext.putProperty("com.aceita.cpf.funcionario.repetido",Boolean.TRUE);

    }
}


