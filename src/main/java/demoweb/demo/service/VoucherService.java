package demoweb.demo.service;

import demoweb.demo.entity.Voucher;
import demoweb.demo.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Optional<Voucher> getVoucherById(Integer id) {
        return voucherRepository.findById(id);
    }

    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Integer id, Voucher voucherDetails) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
        voucher.setCode(voucherDetails.getCode());
        voucher.setDescription(voucherDetails.getDescription());
        voucher.setDiscountPercent(voucherDetails.getDiscountPercent());
        voucher.setMaxUses(voucherDetails.getMaxUses());
        voucher.setStartDate(voucherDetails.getStartDate());
        voucher.setEndDate(voucherDetails.getEndDate());
        voucher.setIsActive(voucherDetails.getIsActive());
        return voucherRepository.save(voucher);
    }

    public void deleteVoucher(Integer id) {
        voucherRepository.deleteById(id);
    }
}