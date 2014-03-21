/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.algorithms.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/*
import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkException;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_tran;
*/

/**
 * Class using the JNI interface of GLPK solver to solve LP and ILP problems.
 * <p/>
 * To see how problems and models should be set out please see:
 * http://www.gnu.org/software/glpk/#TOCdownloading
 *
 * @author Juan Felipe Botero
 * @since 02-12-2010
 */
public class LpSolver {
    private Map<String, Double> solverResult = new LinkedHashMap<String, Double>();
    private Map<String, Double> solverResultFlow = new LinkedHashMap<String, Double>();
    private boolean problemFeasible = false;

    /**
     * @return solver result for "HHVNE_model" and "VNE-Model-NodeMapping"
     */
    public Map<String, Double> getSolverResult() {
        return solverResult;
    }

    /**
     * @return solver result for "HHVNE_model" and "VNE-Model-NodeMapping"
     */
    public Map<String, Double> getSolverResultFlow() {
        return solverResultFlow;
    }

    /**
     * @return boolean indicating whether the proble is feasible
     */
    public boolean problemFeasible() {
        return problemFeasible;
    }

    public LpSolver() {
        solverResult = new LinkedHashMap<String, Double>();
        solverResultFlow = new LinkedHashMap<String, Double>();
    }

    /**
     * Solves the LP problem (In this case a Multi-commodity Flow Problem)
     *
     * @param path          System path where models anda data files are stored
     * @param modelFileName Name of the model
     * @param dataFileName  Name of the data file
     */
    public void solve(String path, String modelFileName, String dataFileName) {
        throw new AssertionError("Properly install gplk-java and uncomment the code in this class");
        /*
        initialize();
		glp_prob lp = null;
		glp_tran tran;
		int ret, n, i, status;
		String name;
		String[] tempVabSplitter;
		double val;
		problemFeasible = true;
		try {
		lp = GLPK.glp_create_prob();
		tran = GLPK.glp_mpl_alloc_wksp();
		ret = GLPK.glp_mpl_read_model(tran, path + modelFileName, 1);
		if (ret != 0) {
		GLPK.glp_mpl_free_wksp(tran);
		GLPK.glp_delete_prob(lp);
		throw new RuntimeException("Model file not found: " + path
		+ modelFileName);
		}
		ret = GLPK.glp_mpl_read_data(tran, path + dataFileName);
		if (ret != 0) {
		GLPK.glp_mpl_free_wksp(tran);
		GLPK.glp_delete_prob(lp);
		throw new RuntimeException("Data file not found: " + path
		+ dataFileName);
		}
		// generate model
		GLPK.glp_mpl_generate(tran, null);
		// build model
		GLPK.glp_mpl_build_prob(tran, lp);
		// Solve the problem
		GLPK.glp_simplex(lp, null);
		// postsolve the problem

*/
		/*
		* Lines to primary test the algorithm behavior
		*/

        /*
		ret = GLPK.glp_mpl_postsolve(tran, lp, GLPKConstants.GLP_SOL);
		status = GLPK.glp_get_status(lp);

		// FIXME: For future work see which is the link causing bottleneck

		// free memory
		n = GLPK.glp_get_num_cols(lp);
		if (modelFileName.equals(Consts.LP_NODEMAPPING_MODEL)) {// For the
		// node
		// mapping
		// phase
		for (i = 1; i <= n; i++) {
		name = GLPK.glp_get_col_name(lp, i); // Variable name
		// following the
		// model
		val = GLPK.glp_get_col_prim(lp, i); // Value for the
		// variable in the
		// optimal solution
		tempVabSplitter = name.split("\\[");
		if (tempVabSplitter[0].equals("lambda")) {
		solverResult.put(name, val);
		}
		if (tempVabSplitter[0].equals("flow")) {
		solverResultFlow.put(name, val);
		}

		}
		} else {// For the link mapping phase
		for (i = 1; i <= n; i++) {
		name = GLPK.glp_get_col_name(lp, i);
		val = GLPK.glp_get_col_prim(lp, i);
		solverResult.put(name, val);
		}
		}

		if (ret != 0 || status != GLPKConstants.GLP_OPT) {
		problemFeasible = false;
		}
		GLPK.glp_mpl_free_wksp(tran);
		GLPK.glp_delete_prob(lp);
		} catch (GlpkException ex) {
		ex.printStackTrace();
		}*/
    }

    public static void initialize() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // try to load Windows library
                System.loadLibrary("glpk_4_44_java");
            } else {
                // try to load Linux library
                System.loadLibrary("glpk_java");
            }
        } catch (UnsatisfiedLinkError e) {
            System.err
                    .println("The dynamic link library for GLPK for Java could not be "
                            + "loaded.\nConsider using\njava -Djava.library.path=");
            throw e;
        }
    }

}