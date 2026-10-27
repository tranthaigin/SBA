/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [OrchidID]
      ,[OrchidName]
      ,[IsNatural]
      ,[OrchidDescription]
      ,[OrchidCategory]
      ,[IsAttractive]
      ,[OrchidURL]
  FROM [OrchidManagementDB].[dbo].[Orchid]

  USE OrchidManagementDB;
GO

INSERT INTO Orchid
    (orchidName, isNatural, orchidDescription, orchidCategory, isAttractive, orchidURL)
VALUES
(
    N'White Phalaenopsis',
    1,
    N'White Phalaenopsis moth orchid with a dark pink and yellow center.',
    N'Phalaenopsis',
    1,
    N'https://upload.wikimedia.org/wikipedia/commons/4/49/Phalaenopsis_Orchid.jpg'
),
(
    N'Cattleya Trianaei',
    1,
    N'Large pale lavender Cattleya orchid with a ruffled magenta and yellow lip.',
    N'Cattleya',
    1,
    N'https://cdn.shopify.com/s/files/1/0959/4313/1445/files/75c23c75-bbc0-4990-89e7-880ca1ae398a__83491.jpg?v=1774571823'
),
(
    N'Pink Dendrobium',
    1,
    N'Pink and white Dendrobium orchid with clusters of flowers along the stem.',
    N'Dendrobium',
    1,
    N'https://4.bp.blogspot.com/-_Ny7GGEcHVM/Vh0DIHcewVI/AAAAAAAAAN8/lT07yDlah8Q/s1600/Dendrobium%2BOrchid%2BCare.jpg'
),
(
    N'Cooktown Orchid',
    1,
    N'Purple Cooktown orchid, also known as Dendrobium bigibbum.',
    N'Dendrobium',
    1,
    N'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Cooktown_orchids_and_bud.jpg/500px-Cooktown_orchids_and_bud.jpg'
),
(
    N'Lady Slipper Orchid',
    1,
    N'Paphiopedilum orchid characterized by its distinctive slipper-shaped pouch.',
    N'Paphiopedilum',
    1,
    N'https://secretgarden.ro/cdn/shop/products/image_71cd5445-7480-42e6-a6ad-c091e4097dd4.jpg?v=1676106611&width=3840'
),
(
    N'Dancing Lady Orchid',
    1,
    N'Yellow Oncidium orchid whose flowers resemble dancing ladies.',
    N'Oncidium',
    1,
    N'https://images.squarespace-cdn.com/content/v1/64c524cb6ca49752316125a9/a2f5f5f8-1a23-4289-a9fc-f974043c9431/Oncidium%2BAlliance.jpg?format=2500w'
),
(
    N'Orange Epidendrum',
    1,
    N'Orange Epidendrum orchid with clusters of vivid orange and yellow flowers.',
    N'Epidendrum',
    1,
    N'https://media.casaegiardino.it/images/2024/09/epidendrum-07.jpg'
),
(
    N'Miltonia Pansy Orchid',
    1,
    N'Magenta and white Miltonia orchid commonly known as the pansy orchid.',
    N'Miltonia',
    1,
    N'https://www.mainau.de/sites/default/files/styles/squared_small/public/2025-03/5-miltonia-vko.jpg.webp?itok=-Ehee3V5'
),
(
    N'Yellow Cymbidium',
    1,
    N'Yellow Cymbidium orchid with deep red markings on the flower lip.',
    N'Cymbidium',
    1,
    N'https://www.trianglenursery.co.uk/pictures/products/medium/CYMBIDIUM-ORCHID-COOKSBRIDGE-60cm.jpg'
),
(
    N'Pink Veined Phalaenopsis',
    0,
    N'Cultivated Phalaenopsis hybrid with pink veins and a deep magenta center.',
    N'Phalaenopsis Hybrid',
    1,
    N'https://www.biosphaere-potsdam.de/fileadmin/_processed_/b/b/csm_Online-Ratespiel_Motiv-Phalaenopsis_1500px_0678bbccf0.jpg'
);
GO