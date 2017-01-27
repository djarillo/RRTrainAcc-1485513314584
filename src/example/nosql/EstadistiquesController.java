package example.nosql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import example.nosql.model.Coordenada;
import example.nosql.service.VcapServices;

@Controller
public class EstadistiquesController {

	private VcapServices vcap;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public String showStatistics(ModelMap model) {
		String sql = "select ITERATION_ID, ACCURACY from DASH7707.RR_ACCURACY";
		ResultSet rs;
		List<Coordenada> coordCont = LazyList.decorate(new ArrayList<Coordenada>(), FactoryUtils.instantiateFactory(Coordenada.class));
		
		vcap = VcapServices.getVcapServices();
		
		Statement stmt = null;
		
		Connection conn = null;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			conn = DriverManager.getConnection(vcap.getDdbCred().getJdbcUrl(), vcap.getDdbCred().getUsername(), vcap.getDdbCred().getPassword());
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				Coordenada coordenada = new Coordenada();
				coordenada.setxCoord(rs.getFloat("ITERATION_ID"));
				coordenada.setyCoord(rs.getFloat("ACCURACY"));
				coordCont.add(coordenada);
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		model.addAttribute("coordenadas", coordCont);
		
		return "estadistiques";
	}
}
