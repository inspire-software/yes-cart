package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.promotion.PromotionCouponCodeGenerator;
import org.yes.cart.service.domain.PromotionCouponService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 03/06/2014
 * Time: 18:35
 */
public class PromotionCouponServiceImpl extends BaseGenericServiceImpl<PromotionCoupon> implements PromotionCouponService {

    private final GenericDAO<Promotion, Long> promotionDao;
    private final PromotionCouponCodeGenerator couponCodeGenerator;

    public PromotionCouponServiceImpl(final GenericDAO<PromotionCoupon, Long> promotionCouponDao,
                                      final GenericDAO<Promotion, Long> promotionDao,
                                      final PromotionCouponCodeGenerator couponCodeGenerator) {
        super(promotionCouponDao);
        this.promotionDao = promotionDao;
        this.couponCodeGenerator = couponCodeGenerator;
    }

    /** {@inheritDoc} */
    public List<PromotionCoupon> findByPromotionId(Long promotionId) {
        return getGenericDao().findByNamedQuery("COUPONS.BY.PROMOTION.ID", promotionId);
    }

    /** {@inheritDoc} */
    public void create(Long promotionId, String couponCode, int limit, int limitPerUser) {

        final List<Object> promoIdAndCode = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.AND.CODE.BY.COUPON.CODE", couponCode);
        if (!promoIdAndCode.isEmpty()) {

            final Pair<Long, String> promoIdAndCodePair = (Pair<Long, String>) promoIdAndCode.get(0);

            if (promoIdAndCodePair.getFirst().equals(promotionId)) {
                return; // we already have this
            }
            throw new IllegalArgumentException("Coupon code '" + couponCode + "' already used in promotion: " + promoIdAndCodePair.getSecond());
        }

        final Promotion promotion = this.promotionDao.findById(promotionId);
        if (promotion == null || !promotion.isCouponTriggered()) {
            throw new IllegalArgumentException("Coupon code '" + couponCode + "' cannot be added to non-coupon promotion: " + promotion.getCode());
        }
        final PromotionCoupon coupon = getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
        coupon.setPromotion(promotion);
        coupon.setCode(couponCode);
        coupon.setUsageLimit(limit);
        coupon.setUsageLimitPerCustomer(limitPerUser);
        coupon.setUsageCount(0);

        this.getGenericDao().saveOrUpdate(coupon);

    }

    /** {@inheritDoc} */
    public void create(Long promotionId, int couponCount, int limit, int limitPerUser) {

        final Promotion promotion = this.promotionDao.findById(promotionId);
        if (promotion == null || !promotion.isCouponTriggered()) {
            throw new IllegalArgumentException("Coupon codes cannot be added to non-coupon promotion: " + promotion.getCode());
        }

        for (int i = 0; i < couponCount; i++) {

            String couponCode;
            List<Object> promoIdAndCode;

            do {
                couponCode = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
                promoIdAndCode = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.AND.CODE.BY.COUPON.CODE", couponCode);
            } while (!promoIdAndCode.isEmpty());

            final PromotionCoupon coupon = getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
            coupon.setPromotion(promotion);
            coupon.setCode(couponCode);
            coupon.setUsageLimit(limit);
            coupon.setUsageLimitPerCustomer(limitPerUser);
            coupon.setUsageCount(0);

            this.getGenericDao().saveOrUpdate(coupon);

        }

    }

    /** {@inheritDoc} */
    public PromotionCoupon findValidPromotionCoupon(String coupon, String customerEmail) {

        // Get enabled coupon code usage limit of which is greater than usage count
        final Date now = new Date();
        final PromotionCoupon couponEntity = getGenericDao().findSingleByNamedQuery("ENABLED.COUPON.BY.CODE",
                coupon, true, now, now);
        if (couponEntity == null) {
            return null;
        }

        // if we have customer usage limit
        if (couponEntity.getUsageLimitPerCustomer() > 0) {
            final List<Object> count = getGenericDao().findQueryObjectByNamedQuery("COUPON.USAGE.BY.ID.AND.EMAIL", couponEntity.getPromotioncouponId(), customerEmail);
            if (!count.isEmpty()) {

                final Number usage = (Number) count.get(0);
                // valid coupon only when we have not exceeded the limit
                if (usage.intValue() >= couponEntity.getUsageLimitPerCustomer()) {
                    return null;
                }

            }
        }

        return couponEntity;
    }

    /** {@inheritDoc} */
    public void updateUsage(final PromotionCoupon promotionCoupon, final int offset) {

        final List<Object> count = getGenericDao().findQueryObjectByNamedQuery("COUPON.USAGE.BY.COUPON.ID", promotionCoupon.getPromotioncouponId());

        int usage = offset;
        if (!count.isEmpty()) {

            final Number usageRecordsCount = (Number) count.get(0);
            usage += usageRecordsCount.intValue();

        }

        promotionCoupon.setUsageCount(usage);
        getGenericDao().saveOrUpdate(promotionCoupon);

    }
}
