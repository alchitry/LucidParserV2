package com.alchitry.labs.hardware.pinout

object AuPinConverter : PinConverter {
    override fun AcfToFPGAPin(acfPin: String): String? =
        when (acfPin) {
            "A2" -> "T8"
            "A3" -> "T7"
            "A5" -> "T5"
            "A6" -> "R5"
            "A8" -> "R8"
            "A9" -> "P8"
            "A11" -> "L2"
            "A12" -> "L3"
            "A14" -> "J1"
            "A15" -> "K1"
            "A17" -> "H1"
            "A18" -> "H2"
            "A20" -> "G1"
            "A21" -> "G2"
            "A23" -> "K5"
            "A24" -> "E6"
            "A27" -> "M6"
            "A28" -> "N6"
            "A30" -> "H5"
            "A31" -> "H4"
            "A33" -> "J3"
            "A34" -> "H3"
            "A36" -> "J5"
            "A37" -> "J4"
            "A39" -> "K3"
            "A40" -> "K2"
            "A42" -> "N9"
            "A43" -> "P9"
            "A45" -> "R7"
            "A46" -> "R6"
            "A48" -> "T9"
            "A49" -> "T10"
            "B2" -> "D1"
            "B3" -> "E2"
            "B5" -> "A2"
            "B6" -> "B2"
            "B8" -> "E1"
            "B9" -> "F2"
            "B11" -> "F3"
            "B12" -> "F4"
            "B14" -> "A3"
            "B15" -> "B4"
            "B17" -> "A4"
            "B18" -> "A5"
            "B20" -> "B5"
            "B21" -> "B6"
            "B23" -> "A7"
            "B24" -> "B7"
            "B27" -> "C7"
            "B28" -> "C6"
            "B30" -> "D6"
            "B31" -> "D5"
            "B33" -> "F5"
            "B34" -> "E5"
            "B36" -> "G5"
            "B37" -> "G4"
            "B39" -> "D4"
            "B40" -> "C4"
            "B42" -> "E3"
            "B43" -> "D3"
            "B45" -> "C3"
            "B46" -> "C2"
            "B48" -> "C1"
            "B49" -> "B1"
            "C2" -> "T13"
            "C3" -> "R13"
            "C5" -> "T12"
            "C6" -> "R12"
            "C8" -> "R11"
            "C9" -> "R10"
            "C11" -> "N2"
            "C12" -> "N3"
            "C14" -> "P3"
            "C15" -> "P4"
            "C17" -> "M4"
            "C18" -> "L4"
            "C20" -> "N4"
            "C21" -> "M5"
            "C23" -> "L5"
            "C24" -> "P5"
            "C27" -> "T4"
            "C28" -> "T3"
            "C30" -> "R3"
            "C31" -> "T2"
            "C33" -> "R2"
            "C34" -> "R1"
            "C36" -> "N1"
            "C37" -> "P1"
            "C39" -> "M2"
            "C40" -> "M1"
            "C42" -> "N13"
            "C43" -> "P13"
            "C45" -> "N11"
            "C46" -> "N12"
            "C48" -> "P10"
            "C49" -> "P11"
            "D8" -> "R16"
            "D9" -> "R15"
            "D11" -> "P14"
            "D12" -> "M15"
            "D42" -> "T14"
            "D43" -> "T15"
            "LED0" -> "K13"
            "LED1" -> "K12"
            "LED2" -> "L14"
            "LED3" -> "L13"
            "LED4" -> "M16"
            "LED5" -> "M14"
            "LED6" -> "M12"
            "LED7" -> "N16"
            "RESET" -> "P6"
            "CLOCK" -> "N14"
            "VP" -> "H8"
            "VN" -> "J7"
            "USB_RX" -> "P15"
            "USB_TX" -> "P16"
            "SPI_D0" -> "J13"
            "SPI_D1" -> "J14"
            "SPI_D2" -> "K15"
            "SPI_D3" -> "K16"
            "SPI_SCK" -> "E8"
            "SPI_SS" -> "L12"
            "DDR_DQ0" -> "A13"
            "DDR_DQ1" -> "B16"
            "DDR_DQ2" -> "B14"
            "DDR_DQ3" -> "C11"
            "DDR_DQ4" -> "C13"
            "DDR_DQ5" -> "C16"
            "DDR_DQ6" -> "C12"
            "DDR_DQ7" -> "C14"
            "DDR_DQ8" -> "D8"
            "DDR_DQ9" -> "B11"
            "DDR_DQ10" -> "C8"
            "DDR_DQ11" -> "B10"
            "DDR_DQ12" -> "A12"
            "DDR_DQ13" -> "A8"
            "DDR_DQ14" -> "B12"
            "DDR_DQ15" -> "A9"
            "DDR_DQS0_P" -> "B15"
            "DDR_DQS0_N" -> "A15"
            "DDR_DQS1_P" -> "B9"
            "DDR_DQS1_N" -> "A10"
            "DDR_DM0" -> "A14"
            "DDR_DM1" -> "C9"
            "DDR_ODT" -> "G11"
            "DDR_RESET" -> "D13"
            "DDR_BA0" -> "E13"
            "DDR_BA1" -> "F15"
            "DDR_BA2" -> "E15"
            "DDR_CK_P" -> "G14"
            "DDR_CK_N" -> "F14"
            "DDR_CKE" -> "D15"
            "DDR_CS" -> "D16"
            "DDR_RAS" -> "D11"
            "DDR_CAS" -> "D14"
            "DDR_WE" -> "E11"
            "DDR_A0" -> "F12"
            "DDR_A1" -> "G16"
            "DDR_A2" -> "G15"
            "DDR_A3" -> "E16"
            "DDR_A4" -> "H11"
            "DDR_A5" -> "G12"
            "DDR_A6" -> "H16"
            "DDR_A7" -> "H12"
            "DDR_A8" -> "J16"
            "DDR_A9" -> "H13"
            "DDR_A10" -> "E12"
            "DDR_A11" -> "H14"
            "DDR_A12" -> "F13"
            "DDR_A13" -> "J15"
            else -> null
        }
}