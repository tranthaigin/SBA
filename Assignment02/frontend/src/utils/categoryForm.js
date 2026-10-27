export const initialCategoryForm = {
  categoryName: "",
  categoryDescription: "",
  parentCategoryId: "",
  active: true,
};

export function validateCategoryForm(form) {
  const categoryName = form.categoryName.trim();
  const categoryDescription = form.categoryDescription.trim();

  if (!categoryName) {
    return "Category name is required.";
  }

  if (categoryName.length > 100) {
    return "Category name must not exceed 100 characters.";
  }

  if (!categoryDescription) {
    return "Description is required.";
  }

  if (categoryDescription.length > 250) {
    return "Description must not exceed 250 characters.";
  }

  if (form.parentCategoryId !== "") {
    const parentId = Number(form.parentCategoryId);
    if (!Number.isInteger(parentId) || parentId < 1 || parentId > 32767) {
      return "Parent Category ID must be a whole number between 1 and 32767.";
    }
  }

  return "";
}

export function toCategoryPayload(form) {
  return {
    categoryName: form.categoryName.trim(),
    categoryDescription: form.categoryDescription.trim(),
    parentCategoryId:
      form.parentCategoryId === "" ? null : Number(form.parentCategoryId),
    active: form.active,
  };
}
