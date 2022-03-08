package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.*;

public class trocaOpcEntregaEmpDestEmpEstEcom implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        TrocaOpcEntregaEmpDestEmpEstEcom(event);
    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {
        TrocaOpcEntregaEmpDestEmpEstEcom(event);
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        TrocaOpcEntregaEmpDestEmpEstEcom(event);
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext ctx) throws Exception {

    }
    private void TrocaOpcEntregaEmpDestEmpEstEcom(PersistenceEvent event) throws Exception {

        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

            DynamicVO iteVO = (DynamicVO) event.getVo();
            BigDecimal nuNota = iteVO.asBigDecimal("NUNOTA");
            String opcEntrega;


            DynamicVO cabVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("CabecalhoNota",nuNota);
            String nuPedidoVtex = cabVO.asString("AD_PEDIDOECOM");
            BigDecimal codEmporig = (BigDecimal) cabVO.getProperty("CODEMP");
            BigDecimal codtipoper = (BigDecimal) cabVO.getProperty("CODTIPOPER");

            if (codEmporig.intValue() == 9 && codtipoper.intValue() == 1009 && nuPedidoVtex != null) {

                FinderWrapper finder = new FinderWrapper("VtexPedidosProdutos", "this.PEDIDO = ?", nuPedidoVtex);
                Collection<DynamicVO> vtxProdsVO = dwfFacade.findByDynamicFinderAsVO(finder);

                if(!vtxProdsVO.isEmpty()) {

                    for (DynamicVO newVtxProdsVO : vtxProdsVO) {

                        String  warehouseid = newVtxProdsVO.asString("WAREHOUSEID");
                        String nuPedido = newVtxProdsVO.asString("PEDIDO");



                        DynamicVO vtxPedVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("VtexPedidos",nuPedido);
                        String FreteId = vtxPedVO.asString("COURIERID");
                        Date dtEntrega = vtxPedVO.asTimestamp("PRAZOENTREGA");

                        DynamicVO vtxFreteVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("VtexFretes",FreteId);
                        int codEmpFrete = vtxFreteVO.asInt("CODPARCTRANSP");

                        DynamicVO vtxArmVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("VtexArmazens",warehouseid);
                        int codEmpArm = vtxArmVO.asInt("CODEMP");

                        ArrayList<Integer> listaEmpresas = new ArrayList<>(Arrays.asList(1,2,3,5,6,9,11,12 ));

                        if (listaEmpresas.contains(codEmpFrete)) {
                            opcEntrega = new String("X") ;
                        }else {
                            opcEntrega = new String("E");
                        }

                        if (!listaEmpresas.contains(codEmpArm)) {
                             codEmpFrete = codEmpArm ;
                        }

                        PersistentLocalEntity LocalEntity = dwfFacade.findEntityByPrimaryKey("ItemNota", new Object[]{nuNota});
                        EntityVO NVO = LocalEntity.getValueObject();
                        DynamicVO IteVO = (DynamicVO) NVO;

                        IteVO.setProperty("AD_CODEMPEST", codEmpArm);
                        IteVO.setProperty("AD_CODEMPDEST",codEmpFrete);
                        IteVO.setProperty("AD_ENTREGA",opcEntrega);
                        IteVO.setProperty("AD_DTENTREGA",dtEntrega);

                        LocalEntity.setValueObject(NVO);


                    }
                }
            }

        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }

    }
}
