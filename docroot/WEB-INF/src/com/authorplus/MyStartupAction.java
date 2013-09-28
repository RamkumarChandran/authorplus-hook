package com.authorplus;

import java.util.List;

import com.liferay.portal.NoSuchResourceActionException;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portlet.expando.NoSuchTableException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;

public class MyStartupAction extends SimpleAction {
	
	
	
	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#SimpleAction()
	 */
	public MyStartupAction() {
		super();
	}

	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#run(String[] ids)
	 */
	public void run(String[] ids) throws ActionException {				
		List<Company> listCompanies;
		try {
			listCompanies = CompanyLocalServiceUtil.getCompanies(false);		
			for(Company company : listCompanies){
				createAuthorshipExpando(company.getCompanyId());
			}				
		} catch (Exception e) {
			
		}		
	}
	
	private void createAuthorshipExpando(long companyId) throws Exception {
		
		ExpandoTable expandoTable = null;
		try {			
			expandoTable = ExpandoTableLocalServiceUtil.getDefaultTable(companyId, User.class.getName());			   
		} catch (NoSuchTableException ex) {			
				expandoTable = ExpandoTableLocalServiceUtil.addDefaultTable(companyId, User.class.getName());			
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		ExpandoColumn expandoColumn = null;
		try {
			expandoColumn = ExpandoColumnLocalServiceUtil.getColumn(companyId, User.class.getName(), expandoTable.getName(), AuthorshipConstants.EXPANDO_AUTHORSHIP);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(expandoColumn == null) {
			try {
				expandoColumn = ExpandoColumnLocalServiceUtil.addColumn(expandoTable.getTableId(), AuthorshipConstants.EXPANDO_AUTHORSHIP, ExpandoColumnConstants.STRING );
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Set permission for user
		if(expandoColumn!=null) {
			Role userRole = RoleLocalServiceUtil.getRole(companyId, RoleConstants.GUEST);
			String[] actions = new String[]{ ActionKeys.VIEW};
			try {
				ResourcePermissionLocalServiceUtil.setResourcePermissions( companyId, 			
																	  	   ExpandoColumn.class.getName(), 
																	  	   ResourceConstants.SCOPE_INDIVIDUAL,        
																	  	   String.valueOf(expandoColumn.getColumnId()), 
																	  	   userRole.getRoleId(), 
																	  	   new String[] { ActionKeys.VIEW});
			} catch (NoSuchResourceActionException ex) {
				ex.printStackTrace();
			}
		}

	}

}