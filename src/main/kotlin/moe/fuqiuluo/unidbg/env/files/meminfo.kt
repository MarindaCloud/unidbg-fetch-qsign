package moe.fuqiuluo.unidbg.env.files

fun fetchMemInfo(): ByteArray {
    return """
        MemTotal:       11444400 kB
        MemFree:          444328 kB
        MemAvailable:    3060808 kB
        Buffers:            2292 kB
        Cached:          2735080 kB
        SwapCached:            0 kB
        Active:          1430388 kB
        Inactive:        6251000 kB
        Active(anon):      28972 kB
        Inactive(anon):  5323536 kB
        Active(file):    1401416 kB
        Inactive(file):   927464 kB
        Unevictable:      192884 kB
        Mlocked:          191652 kB
        SwapTotal:             0 kB
        SwapFree:              0 kB
        Dirty:              2540 kB
        Writeback:             0 kB
        AnonPages:       5136980 kB
        Mapped:          1164384 kB
        Shmem:            221712 kB
        KReclaimable:     871112 kB
        Slab:             673940 kB
        SReclaimable:     180828 kB
        SUnreclaim:       493112 kB
        KernelStack:      108816 kB
        ShadowCallStack:   27232 kB
        PageTables:       186052 kB
        NFS_Unstable:          0 kB
        Bounce:                0 kB
        WritebackTmp:          0 kB
        CommitLimit:     5722200 kB
        Committed_AS:   192359556 kB
        VmallocTotal:   262930368 kB
        VmallocUsed:      303412 kB
        VmallocChunk:          0 kB
        Percpu:            13888 kB
        AnonHugePages:         0 kB
        ShmemHugePages:        0 kB
        ShmemPmdMapped:        0 kB
        FileHugePages:      4096 kB
        FilePmdMapped:      4096 kB
        CmaTotal:         499712 kB
        CmaFree:               0 kB
    """.trimIndent().toByteArray()
}