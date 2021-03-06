/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeraffa;

import dao.Model;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Andrea
 */
@WebServlet(name = "ModificaDocenti", urlPatterns = {"/ModificaDocenti"})
public class ModificaDocenti extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //Context
        ServletContext ctx = getServletContext();
        
        //Where to move
        RequestDispatcher rd = ctx.getRequestDispatcher("/Controller?toDo=tab_docenti");
        
        HttpSession ses = request.getSession();
        
        Model model = new Model();
        
        String idDocente = request.getParameter("idDocente");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        
         String[] corsi = null;
        try {
            corsi=request.getParameterValues("corsi");

        } catch (Exception e) {
            System.out.println("NESSUN CORSO ASSEGNATO AL DOCENTE");
        }
        String elimina = request.getParameter("elimina");
        String salva = request.getParameter("salva");
        String aggiungi = request.getParameter("aggiungi");
        for (int i = 0; corsi != null && i < corsi.length; i++) {
            System.out.println(corsi[i]);
        }
        

        System.out.println("SONO IN MODIFICADOCENTI:" + idDocente + "'" + nome +" - " + cognome + " - " + email );
        
        String sql;
        int id_docente = 0;
        ResultSet rs = null;
        
        if (elimina != null) {
            id_docente = Integer.parseInt(idDocente);
            System.out.println("STO ELIMINANDO IL DOCENTE");
            sql = "UPDATE CorsoDocente SET Attivo = 0 WHERE Docente = " + idDocente;
            model.eseguiNonQuery(sql);
            sql = "UPDATE Docente SET Attivo = 0 WHERE ID_Docente = " + idDocente;
            model.eseguiNonQuery(sql);
            System.out.println("DOCENTE ELIMINATO CORRETTAMENTE");
        }
        else if(salva != null){
            id_docente = Integer.parseInt(idDocente);
            System.out.println("STO AGGIORNANDO IL DOCENTE");
            sql = "UPDATE CorsoDocente SET Attivo = 0 WHERE Docente = " + idDocente;
            model.eseguiNonQuery(sql);
            sql = "UPDATE Docente SET Nome ='" + nome + "',Cognome = '" + cognome + "',email = '" + email + "', attivo = 1 WHERE id_docente =" + idDocente;
            model.eseguiNonQuery(sql);
            for (int i = 0; corsi != null && i < corsi.length; i++) {
                sql = "SELECT * FROM CorsoDocente WHERE Docente = " +id_docente + " AND corso = '" + corsi[i] + "'";
                rs = model.eseguiQuery(sql);
                if (rs.next()){
                    sql = "UPDATE CorsoDocente SET Attivo = 1 WHERE Docente = " + idDocente + " AND Corso = '" + corsi[i] + "'";
                }
                else{
                    sql = "INSERT INTO CorsoDocente(Docente,corso,Attivo) VALUES(" + id_docente + ",'" + corsi[i] + "', 1)";
                }
                model.eseguiNonQuery(sql);
            }
            System.out.println("DOCENTE MODIFICATO CORRETTAMENTE");
        }
        else if(aggiungi != null){
            System.out.println("STO AGGIUNGENDO UN NUOVO DOCENTE");
            sql = "SELECT ID_Docente FROM Docente WHERE email ='" + email + "'";
            rs = model.eseguiQuery(sql);
            if (rs.next()) {
                sql = "UPDATE Docente SET Attivo = 1 WHERE email = '" + email + "'";
                model.eseguiNonQuery(sql);
                id_docente = rs.getInt("id_docente");
            }
            else{
                sql = "INSERT INTO Docente(nome, cognome ,email) VALUES('" + nome + "', '" + cognome + "','" + email + "')";
                model.eseguiNonQuery(sql);
                sql = "SELECT MAX(ID_Docente) AS id FROM Docente";
                rs = model.eseguiQuery(sql);
                if(rs.next())
                    id_docente = rs.getInt("id");
            }
            
            sql = "UPDATE CorsoDocente SET Attivo = 0 WHERE Docente = " + id_docente;
            model.eseguiNonQuery(sql);  
            
            for (int i = 0; corsi != null && i < corsi.length; i++) {
                System.out.println("STO AGGIUNGENDO UN NUOVO CORSODOCENTE");
                sql = "SELECT * FROM CorsoDocente WHERE Docente =" + id_docente + " AND corso = '"+corsi[i]+"'";
                rs = model.eseguiQuery(sql);
                if (rs.next()) {
                    sql = "UPDATE CorsoDocente SET Attivo = 1 WHERE Docente =" + id_docente + " AND corso = '"+corsi[i]+"'"; model.eseguiNonQuery(sql);
                }
                else{
                    sql = "INSERT INTO CorsoDocente(Docente, Corso) VALUES(" + id_docente + ", '" + corsi[i] + "')";
                }
                model.eseguiNonQuery(sql);
                System.out.println("CORSODOCENTE INSERITO CORRETTAMENTE");
            }
            System.out.println("DOCENTE INSERITO CORRETTAMENTE");
            
        }
        else
            System.out.println("ERROR");
        rd.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ModificaDocenti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ModificaDocenti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
