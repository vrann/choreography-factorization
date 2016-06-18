process {
    A00.factorize()
}


service A00 {

    state 0:
        factorize(MatrixBlock A00) {
            U10: MatrixBlock U00I
            L10: MatrixBlock L00I
        }
        state 1;
}


//service -- long-running process. Can restart; stateless; implement method factorize which accepts input data and has methods to retrieve output data; implements terminate;
//channel, implements methods send and receive which maps it to queue; Implements terminate.
//data implements read and write which operates with S3 and filesystem and zip/unzip