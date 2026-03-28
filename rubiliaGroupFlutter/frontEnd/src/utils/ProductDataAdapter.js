export const adaptProductDataForUpdate = (product, updates = {}) => {
    const safeParseFloat = (value) => {
        if (value === undefined || value === null) return 0;
        const parsed = parseFloat(value);
        return isNaN(parsed) ? 0 : parsed;
    };

    const safeParseInt = (value) => {
        if (value === undefined || value === null) return 0;
        const parsed = parseInt(value);
        return isNaN(parsed) ? 0 : parsed;
    };

    const formatDateToISO = (date) => {
        try {
            return date ? new Date(date).toISOString() : new Date().toISOString();
        } catch (e) {
            return new Date().toISOString();
        }
    };

    const safeArray = (arr) => {
        return Array.isArray(arr) ? arr.filter(item => item != null) : [];
    };

    return {
        productName: product.productName || '',
        salePrice: safeParseFloat(product.salePrice),
        comparePrice: safeParseFloat(product.comparePrice),
        quantity: safeParseInt(product.quantity),
        shortDescription: product.shortDescription || '',
        productDescription: product.productDescription || '',
        slug: product.slug || (product.productName ? product.productName.toLowerCase().replace(/\s+/g, '-') : 'unnamed-product'),
        productType: product.productType || 'simple',
        published: product.published !== undefined ? product.published : false,
        disableOutOfStock: product.disableOutOfStock !== undefined ? product.disableOutOfStock : false,
        createdAt: formatDateToISO(product.createdAt),
        updatedAt: formatDateToISO(new Date()),
        idCategories: safeArray(product.productCategories?.map(pc => pc.category?.id)),
        images: safeArray(product.galleries?.map(g => g.image)),
        displayAreas: safeArray(updates.displayAreas || product.displayInfos?.map(info => info.displayArea)),
        salesCount: safeParseFloat(updates.salesCount !== undefined ? updates.salesCount : product.displayInfos?.[0]?.salesCount),
        rating: safeParseFloat(updates.rating !== undefined ? updates.rating : product.displayInfos?.[0]?.rating),
    };
};