package lab;

import mpi.Cartcomm;
import mpi.MPI;

import java.util.Arrays;

public class CannonMethod {
    private static int[] gridCoords = new int[2];

    private static Cartcomm ColComm;

    private static Cartcomm RowComm;

    private static void matrixScatter(int[] matrix, int[] matrixBlock, int dimension, int blockSize) {
        int[] matrixRow = new int[blockSize * dimension];
        if (gridCoords[1] == 0)
            ColComm.Scatter(matrix, 0, blockSize * dimension, MPI.INT, matrixRow, 0, blockSize * dimension, MPI.INT, 0);
        for (int i = 0; i < blockSize; i++) {
            int[] subRow = Arrays.copyOfRange(matrixRow, i * dimension, matrixRow.length);
            int[] subRowRes = new int[blockSize];

            RowComm.Scatter(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            System.arraycopy(subRowRes, 0, matrixBlock, i * blockSize, blockSize);
        }
    }

    public static void calculate(String[] args, int dimension) {
        MPI.Init(args);

        int processNumber = MPI.COMM_WORLD.Rank();
        int threadsNumber = MPI.COMM_WORLD.Size();
        int gridSize = (int) Math.sqrt(threadsNumber);

        if (threadsNumber != gridSize * gridSize) {
            if (processNumber == 0)
                System.out.println("Метод Кэннона [" + dimension + "x" + dimension + "]: ");
            MPI.Finalize();
            return;
        }

        Cartcomm gridComm;

        int blockSize = dimension / gridSize;

        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");

        int[] BlockA = new int[blockSize * blockSize];
        int[] BlockB = new int[blockSize * blockSize];
        int[] BlockC = new int[blockSize * blockSize];

        long time = 0L;

        if (processNumber == 0) {
            A.fillRandom(5);
            B.fillRandom(5);
            time = System.currentTimeMillis();
        }

        boolean[] subdims = new boolean[2];

        gridComm = MPI.COMM_WORLD.Create_cart(new int[]{gridSize, gridSize}, new boolean[]{false, false}, true);

        gridCoords = gridComm.Coords(processNumber);

        subdims[1] = true;
        RowComm = gridComm.Sub(subdims);

        subdims[0] = true;
        subdims[1] = false;
        ColComm = gridComm.Sub(subdims);

        matrixScatter(A.matrix, BlockA, dimension, blockSize);
        matrixScatter(B.matrix, BlockB, dimension, blockSize);

        if (gridCoords[0] != 0) {
            int nextProcess = gridCoords[1] - gridCoords[0];
            if (nextProcess < 0)
                nextProcess += gridSize;
            RowComm.Sendrecv_replace(BlockA, 0, blockSize * blockSize, MPI.INT, nextProcess, 0, MPI.ANY_SOURCE, 0);
        }

        if (gridCoords[1] != 0) {
            int nextProcess = gridCoords[0] - gridCoords[1];
            if (nextProcess < 0) nextProcess += gridSize;
            ColComm.Sendrecv_replace(BlockB, 0, blockSize * blockSize, MPI.INT, nextProcess, 1, MPI.ANY_SOURCE, 1);
        }

        MPI.COMM_WORLD.Barrier();

        for (int i = 0; i < blockSize; i++)
            for (int j = 0; j < blockSize; j++)
                for (int k = 0; k < blockSize; k++)
                    BlockC[i * blockSize + j] += BlockA[i * blockSize + k] * BlockB[k * blockSize + j];

        for (int iter = 0; iter < gridSize - 1; iter++) {
            int nextProcess = gridCoords[1] - 1;
            if (nextProcess < 0)
                nextProcess += gridSize;
            RowComm.Sendrecv_replace(BlockA, 0, blockSize, MPI.INT, nextProcess, 0, MPI.ANY_SOURCE, 0);

            nextProcess = gridCoords[0] - 1;
            if (nextProcess < 0)
                nextProcess += gridSize;

            ColComm.Sendrecv_replace(BlockB, 0, blockSize, MPI.INT, nextProcess, 1, MPI.ANY_SOURCE, 1);

            for (int i = 0; i < blockSize; i++)
                for (int j = 0; j < blockSize; j++)
                    for (int k = 0; k < blockSize; k++)
                        BlockC[i * blockSize + j] += BlockA[i * blockSize + k] * BlockB[k * blockSize + j];
        }

        int[] resultRow = new int[dimension * blockSize];
        for (int i = 0; i < blockSize; i++) {
            int[] subRow = Arrays.copyOfRange(BlockC, i * blockSize, BlockC.length);
            int[] subRowRes = new int[gridSize * blockSize];

            RowComm.Gather(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            System.arraycopy(subRowRes, 0, resultRow, i * dimension, gridSize * blockSize);
        }

        if (gridCoords[1] == 0)
            ColComm.Gather(resultRow, 0, blockSize * dimension, MPI.INT, C.matrix, 0, blockSize * dimension, MPI.INT, 0);

        if (processNumber == 0) {
            System.out.print("Cannon's method [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms\n");
        }
        MPI.Finalize();
    }
}