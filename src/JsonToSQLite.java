import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class JsonToSQLite {

	class TableObjs{

		List<TableObjs> tableObjsList = new ArrayList<TableObjs>();

		List<Object> objs = new ArrayList<Object>();

		private String sqlCreateString;

		public TableObjs(List<Object> objs) {
			this.objs = new ArrayList<Object>(objs);
		}

		void addChild(TableObjs tableObjs){
			this.tableObjsList.add(tableObjs);
		}

		TableObjs getChild(int index){
			return tableObjsList.get(index);
		}
		
		public List<TableObjs> getChildList() {
			return tableObjsList;
		}

		
		List<Object> getObjList(){
			return objs;
		}

		Field[] getFields(){
			return objs.get(0).getClass().getDeclaredFields();
		}

		void setListObj(List<Object> objs){
			this.objs = new ArrayList<Object>(objs);
		}

		Object getValueOrThrow(Field field){
			field.setAccessible(true);
			Object val = null;
			try {
				val = field.get(objs);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return val;
		}

		Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException{
			field.setAccessible(true);
			Object val = null;
			val = field.get(objs);
			return val;
		}

		public void setSqlCreateString(String sqlCreateString) {
			this.sqlCreateString = new String(sqlCreateString);
		}
		
		public String getSqlCreateString(){
			return sqlCreateString;
		}


	}
	
	/*
	 * 
	 * 
	 * 
	 */
	//Map<String, FieldObjs> fieldObjs = new LinkedHashMap<String, FieldObjs>();

	/*
	 * 
	 * 
	 * 
	 */
	String getFieldAsType(Field field){
		String typeField = null;
		Class<?> type = field.getType();
		
		switch (type.toString()) {
		case "class java.lang.String":
			typeField = "TEXT";
			break;
		case "class java.lang.Float":
			typeField = "FLOAT";
			break;
		case "class java.lang.Double":
			typeField = "REAL";
			break;
		case "class java.lang.Integer":
			typeField = "INTEGER";
			break;
		case "class java.lang.Boolean":
			typeField = "BOOLEAN";
			break;
		default:
			//throw new IllegalArgumentException("Invalid type of Value");
		}
		return typeField;
	}

	
	
	/*
	 * 
	 * 
	 * 
	 */
	TableObjs createColumns(TableObjs tableObjs) throws IllegalArgumentException, IllegalAccessException{
		List<?> dataList = tableObjs.getObjList();
		Field[] fields = tableObjs.getFields();
		String tableName = dataList.get(0).getClass().getSimpleName();
		String strSqlCreate = "CREATE TABLE " + tableName + " ("
							+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, ";
		for (Field field : fields) {
			System.out.println(field.getName() + "=>" + field.getType());
			if(getFieldAsType(field) != null){
				strSqlCreate += field.getName() + " " + getFieldAsType(field) + ", ";
			}else{
				ArrayList<Object> valList = new ArrayList<Object>();
				strSqlCreate += field.getName() + "_id INTEGER, ";
				for (Object dataItem : dataList) {
					for (Field nestedField : fields) {
						if(nestedField.equals(field)){
							nestedField.setAccessible(true);
							Object val = nestedField.get(dataItem);
							//System.out.println(val);
							valList.add(val);
						}
					}
				}

				tableObjs.addChild( createColumns(new TableObjs(valList)) );
			}
		}
		strSqlCreate = strSqlCreate.substring(0, strSqlCreate.length() - 2);
		strSqlCreate += ");";
		System.out.println(strSqlCreate);
		tableObjs.setSqlCreateString(strSqlCreate);
		return tableObjs;
	}


	void createTables(TableObjs fieldObjs, Statement statement) throws SQLException, IllegalArgumentException, IllegalAccessException{
		for (TableObjs fo : fieldObjs.getChildList()) {
			createTables(fo, statement);
		}

		String tableName = fieldObjs.getObjList().get(0).getClass().getSimpleName();
		System.out.println( "tableName=" + tableName );
		statement.executeUpdate("drop table if exists " + tableName);
		statement.executeUpdate(fieldObjs.getSqlCreateString());
		for (Object dataItem : fieldObjs.getObjList()) {
			String sqlValuesStr = "";
			if(dataItem == null){
				for (Field field : fieldObjs.getFields()) {
					sqlValuesStr += "'null',";	// when related fields not found 
				}
			}else{
				for (Field field : fieldObjs.getFields()) {
					if(getFieldAsType(field) == null){
						sqlValuesStr += "'" + 0 + "',";
					}else{
						field.setAccessible(true);
						Object val = field.get(dataItem);
						val = val != null ? val : "";
						val = val.toString().replace("'", "''");
						sqlValuesStr += "'" + val + "',";
					}
				}
			}
			//System.out.println(sqlValuesStr);
			sqlValuesStr = "?, "	+ sqlValuesStr.substring(0, sqlValuesStr.length() - 1);
			String sqlCreateStr = "INSERT INTO " + tableName + " VALUES(" + sqlValuesStr + ");";
			//System.out.println(sqlCreateStr);
			statement.execute(sqlCreateStr);
			//ResultSet rs = statement.executeQuery("select last_insert_rowid();");
			//System.out.println(rs.getInt(1));
		}
	}

	//
	//
	//
	void printTable(TableObjs tableObjs){
		for (TableObjs t : tableObjs.getChildList()) {
			printTable(t);
		}
		System.out.println(">>>" + tableObjs.getSqlCreateString());
		System.out.println(">>>" + tableObjs.getObjList().size());
		for(Object obj : tableObjs.getObjList() ){
			if(obj == null){
				System.out.println(">>> null");
			}
		}
	}

	/*
	 * 
	 * 	Convert List of Object to SQLite DataBase
	 * 
	 */
	void createDb(String dbName, String tableName, List<Object> dataList) throws IllegalArgumentException, IllegalAccessException{


		TableObjs tableObjs = createColumns(new TableObjs(dataList));
		
		//printTable(tableObjs);

		Connection connection = null;
		try{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			statement.execute("BEGIN TRANSACTION;");
			createTables(tableObjs, statement);
			statement.execute("COMMIT TRANSACTION;");

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
		}
		
	}

	
	
	
}
