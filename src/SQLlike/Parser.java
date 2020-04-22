package SQLlike;

/*
Author : nickpsal
*/

import java.util.ArrayList;


public class Parser {
        private Lex lex;
        private Token token;
        private ArrayList<String> CSVfields;
        private ArrayList<String> qFields;
        private String selected_field_to_check;
        private ArrayList<String> selected_values;

        
        public Parser (String filename)
        {
            lex = new Lex(filename);
            token = lex.nextToken();
            //Create ArrayList for saving the fields that the user put
            //to the code of the txt file and the fields on the csv file
            CSVfields = new ArrayList<>();
            qFields = new ArrayList<>();
            //The criteria of the query
            selected_values = new ArrayList<>();
            //Starts the program
            program();   
        }
       
        
        private void error(String s)
        {
            System.out.format("error in line %d: %s\n",token.line,s);
            System.exit(0);
        }
    
        //SQLlite rules
        private void program()
        {
            statement();
            while (token.type.name().equals("semicolTK")) {
                token = lex.nextToken();
                if(token.type.name()=="eofTK"){
                    break;
                }
                statement();
            }
        }        
        // choose Select or Join if the user on txt file
        //put select it will run select 
        //if he put Join it will rum Join
        //Anything else he put except Select or Join 
        //it will show Error
        private void statement()
        {
            if (token.type.name().equals("selectTK")) {
                token = lex.nextToken();
                select();
            }else if (token.type.name().equals("joinTK")){
                token = lex.nextToken();
                join();
            }else {
                error(token.data);
            }
        }
        
        //Select code
        private void select()
        {
            String table_name = "" ; 
            Table table = null;
            columns();
            if (token.type.name().equals("fromTK")) {
                token = lex.nextToken();
                //Έλεγχος αν το όνομα του πίνακα που δώσαμε
                //στο query υπάρχει σαν csv αρχείο
                table = new Table(token.data);
                // fields from csv file 
                // Store them in Arraylist
                CSVfields.addAll(table.fields);
                table();
                where_part();
                for (int i = 0; i<qFields.size(); i++) {
                    if (!table.fields.contains(qFields.get(i))) {
                        error(" Cant find field " + qFields.get(i));
                    }
                }
                if (token.type.name().equals("createTK")) {
                    token = lex.nextToken();
                    table_name = token.data;                   
                    table();
                }else {
                    error(token.data);
                }
            }else {
                error(token.data);
            }
            new Table(table_name,table,qFields,selected_field_to_check,selected_values);
        }
                    
        //Where code
        private void where_part()
        {
            if (token.type.name().equals("whereTK")) {
                token = lex.nextToken();
                selected_field_to_check = token.data;
                for (int i = 0; i<CSVfields.size(); i++) {
                    if (!CSVfields.contains(token.data)) {
                        error(" Cant find field " + token.data);
                    }
                }
                condition();
            }
        }
        
        //conditions
        private void condition()
        {
            column();
            if (token.type.name().equals("equalTK")) {
                token = lex.nextToken();
                value();
                while (token.type.name().equals("orTK")) {
                    token = lex.nextToken();
                    value();
                }
            }else {
                error(token.data);
            }
        }       
        
        //CSV Columns
        private void columns()
        {
            //fields from query code on txt file
            //I store them on ArrayList
            qFields.add(token.data);
            column();
            while(token.type.name().equals("commaTK")) {
                token = lex.nextToken();
                qFields.add(token.data);
                column();
            }
        }        
        
        //Join code
         private void join()
        {
            String table_name="";
            Table table_1 = null;
            Table table_2 = null;
            String selected_field_1="";
            String selected_field_2="";

            table_1 = new Table(table());
            if (token.type.name() == "commaTK")
            {
                token = lex.nextToken();
                table_2 = new Table(table());
                if (token.type.name() == "whereTK")
                {
                    token = lex.nextToken();
                    selected_field_1=column();
                    if (token.type.name() == "equalTK")
                    {
                        token = lex.nextToken();
                        selected_field_2=column();
                        if (token.type.name() == "createTK")
                        {
                            token = lex.nextToken();
                            table_name = new String(table());
                        } else error ("create expected");
                    } else error("symbol == expected");
                } else error("where expected");
            } else error("comma expected");

            new Table(table_name, table_1, table_2, selected_field_1, selected_field_2);
        }
        
        //Table name code
        private String table()
        {
            String value="";
            if(token.type.name().equals("stringTK")){
                value = token.data.toString();
                token = lex.nextToken();
            }else {
                error(token.data);
            }
            return value;
        }
        
        //column 
        private String column()
        {
            String value = "";
            if (token.type.name().equals("stringTK")) {
                value = token.data;
                token = lex.nextToken();
            }else {
                error(token.data);
            }
            return value;
        }
        
        //Value code
        private String value()
        {
            String value = "";
            if (token.type.name().equals("quoatedStringTK")) {
                //Remove "" from values 
                selected_values.add(token.data.replace("\"", ""));
                token = lex.nextToken();
                value = token.data;
            }else {
                error(token.data);
            }
            return value;
        }
}
